package is.yarr.beerscanner.service;

import is.yarr.beerscanner.model.Bar;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class BarWebpageScraperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BarWebpageScraperService.class);

    private static final String JS_PROCESSING_SCRIPT = """
            /**
             * Recursively extracts and concatenates all text content from a given HTML node
             * and its descendants.
             * - Skips <script> and <style> tags and their content.
             * - Normalizes whitespace: multiple spaces/newlines become a single space,
             *   and leading/trailing whitespace is trimmed from each text segment and the final result.
             * - Joins text from different child nodes or inline elements with a single space
             *   to maintain readability.
             *
             * @param {Node} node The HTML node (Element or Text node) to process.
             * @returns {string} The concatenated and normalized text content from the node.
             */
            function extractTextRecursively(node) {
                if (!node) {
                    return "";
                }
            
                // If it's a text node, normalize its content and return.
                if (node.nodeType === Node.TEXT_NODE) {
                    // Replace multiple whitespace characters (including newlines) with a single space, then trim.
                    return node.textContent.replace(/\\s\\s+/g, ' ').trim();
                }
            
                // If it's an element node, process its children.
                if (node.nodeType === Node.ELEMENT_NODE) {
                    const element = node;
                    const tagName = element.tagName.toLowerCase();
            
                    if (tagName === 'script' || tagName === 'style') {
                        return "";
                    }
            
                    let textSegments = [];
                    for (const childNode of element.childNodes) {
                        const childText = extractTextRecursively(childNode);
                        if (childText) {
                            textSegments.push(childText);
                        }
                    }
            
                    // Join the text segments from children with a single space.
                    // Then, trim any leading/trailing space from this element's aggregated text.
                    // This ensures words from adjacent text nodes or elements are spaced correctly
                    // but avoids extra spaces if an element starts/ends with block children.
                    return textSegments.join(' ').trim();
                }
            
                // Ignore other node types (comments, CData, processing instructions, etc.)
                return "";
            }
            
            function createResponse(data, error) {
                if (data) {
                    return {
                        status: 'success',
                        data: JSON.stringify(data)
                    };
                }
            
                return {
                    status: 'error',
                    message: error.message || 'A JS error occurred'
                };
            }
            
            /**
             * Takes an XPath expression, finds all matching HTML elements,
             * extracts all text content from each, and returns a single string.
             * In this string, the text from each top-level matched element is
             * separated by a newline character. This function aims to produce a highly
             * minified text representation suitable for AI processing where token count is critical.
             *
             * @param {string} xpathExpression The XPath expression (e.g., "//p" to get all paragraphs,
             *                                 or "//article//h2" to get all h2s within articles).
             * @returns {string} A string containing all extracted text. Text from different
             *                   top-level elements matched by the XPath is separated by newlines.
             *                   Returns an empty string if no elements match or if matching elements
             *                   contain no text.
             */
            function convertHtmlToMinifiedTextByXPath(xpathExpression) {
                const allTopLevelTexts = [];
                try {
                    // Use ORDERED_NODE_ITERATOR_TYPE to get all matching nodes in document order.
                    const result = document.evaluate(xpathExpression, document, null, XPathResult.ORDERED_NODE_ITERATOR_TYPE, null);
                    let currentElement = result.iterateNext();
            
                    while (currentElement) {
                        if (currentElement.nodeType === Node.ELEMENT_NODE) {
                            const textFromElement = extractTextRecursively(currentElement);
                            if (textFromElement) {
                                allTopLevelTexts.push(textFromElement);
                            }
                        }
                        currentElement = result.iterateNext();
                    }
                } catch (e) {
                    return createResponse(null, e);
                }
            
                return createResponse(allTopLevelTexts, null);
            }
            """;

    /**
     * @return A base64 image of the bar's menu
     */
    /* public String processBarWebpage(Bar bar) {
        var webpageSettings = bar.getWebpageSettings();
        var menuUrl = bar.getMenuUrl();

        var menuXPath = Objects.requireNonNull(webpageSettings.getMenuComponentXPath(), "Menu Component XPath cannot be null");
        var ageVerificationXPath = Objects.requireNonNullElse(webpageSettings.getAgeVerificationXPath(), "");
        var cleanupScript = Objects.requireNonNullElse(webpageSettings.getCleanupScript(), "");

        WebDriver driver = null;
        try {
            var chromeOptions = new ChromeOptions();
//             chromeOptions.addArguments("--headless");

            driver = new RemoteWebDriver(URI.create(System.getenv("SELENIUM_HUB_URL")).toURL(), chromeOptions);

            driver.get(menuUrl);

            // Click age verification button, if present
            if (!ageVerificationXPath.isBlank()) {
                LOGGER.info("Clicking age verification button with XPath: {}", ageVerificationXPath);
                clickElementWhenAvailable(driver, ageVerificationXPath);
            }

            // Wait for the page to load completely
            Thread.sleep(2000);

            if (!cleanupScript.isBlank()) {
                var javascriptExecutor = (JavascriptExecutor) driver;
                LOGGER.info("Cleaning up page with script: {}", cleanupScript);
                javascriptExecutor.executeScript(cleanupScript);
            }

            WebElement menuElement;
            try {
                menuElement = waitForElement(driver, menuXPath);
            } catch (Exception e) {
                System.err.println("Menu element not found with XPath: " + menuXPath);

                var imageBytes = Shutterbug.shootPage(driver, Capture.VERTICAL_SCROLL).getBytes();

                var originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

                var time = String.valueOf(System.currentTimeMillis());

                ImageIO.write(originalImage, "png", Paths.get(time + "_EXCEPTION.png").toFile());

                throw new RuntimeException("Menu element not found", e);
            }

            var imageBytes = Shutterbug.shootElement(driver, menuElement, CaptureElement.VERTICAL_SCROLL).getBytes();

            var originalImage = ImageIO.read(new ByteArrayInputStream(imageBytes));

            var time = String.valueOf(System.currentTimeMillis());

            ImageIO.write(originalImage, "png", Paths.get(time + "_screenshot_original.png").toFile());

            // Resize image to max 1000px
            var resizedImage = BarCheckScheduler.resizeImage(originalImage, 1000);

            ImageIO.write(resizedImage, "png", Paths.get(time + "_screenshot_scaled.png").toFile());

            throw new RuntimeException("Breaking... this to test the screenshot saving functionality");

        } catch (MalformedURLException e) {
            System.err.println("Invalid Selenium Hub URL: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error saving screenshot: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        return null;
    } */

    record HtmlToTextResponse(boolean success, String errorMessage, String data) {
    }

    private String getNullableSimpleName(Object obj) {
        return obj == null ? "null" : obj.getClass().getSimpleName();
    }

    private HtmlToTextResponse processJSResult(Object result) {
        if (!(result instanceof Map)) {
            return new HtmlToTextResponse(false, "Internal processing error: result is not a Map (got a %s). Data: %s".formatted(getNullableSimpleName(result), result), null);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> resultMap = (Map<String, Object>) result;

        var mapStatus = resultMap.get("status");
        if (!(mapStatus instanceof String statusString)) {
            return new HtmlToTextResponse(false, "Internal processing error: status is not a string (got a %s). Data: %s".formatted(getNullableSimpleName(mapStatus), result), null);
        }

        var success = statusString.equals("success");

        if (success) {
            var mapData = resultMap.get("data");
            if (!(mapData instanceof String data)) {
                return new HtmlToTextResponse(false, "Internal processing error: data is not a string (got a %s). Data: %s".formatted(getNullableSimpleName(mapData), result), null);
            }

            return new HtmlToTextResponse(true, null, data);
        } else {
            var mapMessage = resultMap.get("message");
            if (!(mapMessage instanceof String errorMessage)) {
                return new HtmlToTextResponse(false, "Internal processing error: message is not a string (got a %s). Data: %s".formatted(getNullableSimpleName(mapMessage), result), null);
            }

            return new HtmlToTextResponse(false, errorMessage, null);
        }
    }

    public Optional<String> processTextualBarWebpage(Bar bar) {
        var webpageSettings = bar.getWebpageSettings();
        var menuUrl = bar.getMenuUrl();

        if (!webpageSettings.isProcessAsText()) {
            throw new RuntimeException("This shouldn't happen, processAsText is false but this method was called");
        }

        var menuXPath = Objects.requireNonNull(webpageSettings.getMenuComponentXPath(), "Menu Component XPath cannot be null");
        var ageVerificationXPath = Objects.requireNonNullElse(webpageSettings.getAgeVerificationXPath(), "");
        var cleanupScript = Objects.requireNonNullElse(webpageSettings.getCleanupScript(), "");

        WebDriver driver = null;
        try {
            var chromeOptions = new ChromeOptions();
//             chromeOptions.addArguments("--headless");

            driver = new RemoteWebDriver(URI.create(System.getenv("SELENIUM_HUB_URL")).toURL(), chromeOptions);
            driver.get(menuUrl);

            // Click age verification button, if present
            if (!ageVerificationXPath.isBlank()) {
                LOGGER.info("Clicking age verification button with XPath: {}", ageVerificationXPath);
                tryToClickElementWhenAvailable(driver, ageVerificationXPath);
            }

            // Wait for the page to load completely
            Thread.sleep(2000);

            var javascriptExecutor = (JavascriptExecutor) driver;

            if (!cleanupScript.isBlank()) {
                LOGGER.info("Cleaning up page with script: {}", cleanupScript);
                javascriptExecutor.executeScript(cleanupScript);
            }

            try {
                // Ensure the menu is present
                waitForElementPresence(driver, menuXPath);
            } catch (Exception e) {
                LOGGER.error("Menu element not found with XPath: {}", menuXPath);
                throw new RuntimeException("Menu element not found", e);
            }

            Object rawJSResult = javascriptExecutor.executeScript("""
                    %s
                    
                    return convertHtmlToMinifiedTextByXPath(arguments[0]);""".formatted(JS_PROCESSING_SCRIPT),
                    menuXPath);

            var jsResult = processJSResult(rawJSResult);

            if (!jsResult.success) {
                throw new RuntimeException("JS processing error: " + jsResult.errorMessage);
            }

            LOGGER.info("Extracted text data:\n{}", jsResult.data);

            return Optional.of(jsResult.data);
        } catch (Exception e) {
            LOGGER.error("An error occurred while processing the bar webpage", e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        return Optional.empty();
    }

    private WebElement waitForElement(WebDriver driver, String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(15, ChronoUnit.SECONDS));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    }


    private WebElement waitForElementPresence(WebDriver driver, String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(15, ChronoUnit.SECONDS));
        return wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
    }

    private void clickElementWhenAvailable(WebDriver driver, String xpath) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.of(15, ChronoUnit.SECONDS));
        var element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
        element.click();
    }

    private void tryToClickElementWhenAvailable(WebDriver driver, String xpath) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.of(15, ChronoUnit.SECONDS));
            var element = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            element.click();
        } catch (Exception e) {
            LOGGER.warn("Element with XPath {} not clickable: {}", xpath, e.getMessage());
        }
    }

}
