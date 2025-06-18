package is.yarr.beerscanner.scheduler;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.BarCheck;
import is.yarr.beerscanner.service.BarCheckService;
import is.yarr.beerscanner.service.BarService;
import is.yarr.beerscanner.service.BarWebpageScraperService;
import is.yarr.beerscanner.service.NotificationService;
import org.imgscalr.Scalr;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Scheduler for bar checks.
 */
@Component
public class BarCheckScheduler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BarCheckScheduler.class);

    private final BarService barService;
    private final BarCheckService barCheckService;
    private final NotificationService notificationService;
    private final BarWebpageScraperService barWebpageScraperService;

    public BarCheckScheduler(BarService barService, BarCheckService barCheckService, NotificationService notificationService, BarWebpageScraperService barWebpageScraperService) {
        this.barService = barService;
        this.barCheckService = barCheckService;
        this.notificationService = notificationService;
        this.barWebpageScraperService = barWebpageScraperService;
    }

    /**
     * Check all bars daily.
     */
    @Scheduled(cron = "${app.scheduler.bar-check.cron}")
    public void checkAllBars() {
        LOGGER.info("Starting scheduled bar check");

        List<Bar> barsToCheck = barService.getBarsToCheck();
        LOGGER.info("Found {} bars to check", barsToCheck.size());

        for (Bar bar : barsToCheck) {
            try {
                checkBar(bar);
            } catch (Exception e) {
                LOGGER.error("Error checking bar {}: {}", bar.getName(), e.getMessage(), e);
            }
        }

        // Process any pending checks
        processPendingChecks();

        // Process any unsent notifications
        notificationService.processUnsentNotifications();

        LOGGER.info("Completed scheduled bar check");
    }

    /**
     * Check a bar.
     *
     * @param bar the bar to check
     */
    public Optional<BarCheck> checkBar(Bar bar) {
        LOGGER.info("Checking bar: {}", bar.getName());

        try {
            long startTime = System.currentTimeMillis();

            // Get menu content
            var menuContent = getMenuContent(bar);

            // Calculate hash
            var menuHash = calculateHash(menuContent.content);

            long duration = System.currentTimeMillis() - startTime;

            // Create check
            BarCheck check = barCheckService.createCheck(bar, menuContent.content, menuContent.contentType, menuHash, (int) duration);

            // Process check (this will handle if no changes were detected)
            barCheckService.processCheck(check.getId());

            return Optional.of(check);
        } catch (Exception e) {
            LOGGER.error("Error checking bar {}: {}", bar.getName(), e.getMessage(), e);
        }

        return Optional.empty();
    }

    public record MenuContent(String content, String contentType) {}

    /**
     * Get menu content.
     *
     * @param bar the bar
     * @return the menu content
     * @throws IOException if an I/O error occurs
     */
    private MenuContent getMenuContent(Bar bar) throws IOException, URISyntaxException {
        if (bar.getMenuUrl() == null) {
            throw new IllegalArgumentException("Bar has no menu URL");
        }

        String finalMenuUrl = bar.getMenuUrl();

        var webScraping = true;

        // If XPath is provided, navigate to the page and extract the actual menu URL
        // If provided, this means this exact XPath is the menu, and web scraping does NOT need to happen
        if (bar.getMenuXPath() != null && !bar.getMenuXPath().isEmpty()) {
            LOGGER.info("Using XPath to extract menu URL from {}", finalMenuUrl);
            finalMenuUrl = extractMenuUrlUsingXPath(finalMenuUrl, bar.getMenuXPath());
            LOGGER.info("Extracted menu URL: {}", finalMenuUrl);

            webScraping = false;
        }

        // Determine content type
        HeadResponse headResponse = sendHeadRequest(finalMenuUrl);
        LOGGER.info("Content type for {}: {}", finalMenuUrl, headResponse.contentType);
        LOGGER.info("Etag for {}: {}", finalMenuUrl, headResponse.etag);
        // TODO: Handle etag

        String base64;
        String contentType = headResponse.contentType;
        if (headResponse.contentType.contains("application/pdf")) {
            // Handle PDF
            LOGGER.info("Processing PDF menu from {}", finalMenuUrl);
            base64 = processPdfMenu(finalMenuUrl);
        } else if (headResponse.contentType.contains("image/")) {
            // Handle image
            LOGGER.info("Processing image menu from {}", finalMenuUrl);
            base64 = processImageMenu(finalMenuUrl);
        } else if (headResponse.contentType.contains("text/html")) {
            if (!webScraping) {
                throw new RuntimeException("Web scraping required for text/html content type, but XPath was provided.");
            }

            if (!bar.getWebpageSettings().isProcessAsText()) {
                // If the bar has a webpage settings that does not process as text, we need to scrape the webpage
                LOGGER.info("Processing bar webpage for {}", finalMenuUrl);
                contentType = "image/png";
                throw new RuntimeException("Extracted image processing not implemented yet, please implement the barWebpageScraperService.processBarWebpage method.");
//                base64 = barWebpageScraperService.processBarWebpage(bar);
            } else {
                LOGGER.info("Fetching text menu from {}", finalMenuUrl);
                contentType = "text/plain";
                base64 = barWebpageScraperService.processTextualBarWebpage(bar);

                System.out.println("base64 = " + base64);
                System.out.println("contentType = " + contentType);
            }
        } else {
            throw new RuntimeException("Unsupported content type: " + headResponse.contentType);
        }

        return new MenuContent(base64, contentType);
    }

    /**
     * Extract menu URL using XPath.
     *
     * @param pageUrl the page URL
     * @param xpath the XPath
     * @return the menu URL
     * @throws IOException if an I/O error occurs
     */
    private String extractMenuUrlUsingXPath(String pageUrl, String xpath) throws IOException {
        Document doc = Jsoup.connect(pageUrl).get();
        Elements elements = doc.selectXpath(xpath);

        if (elements.isEmpty()) {
            throw new IOException("No element found with XPath: " + xpath);
        }

        Element element = elements.first();
        String href = element.attr("href");

        System.out.println("Raw href: " + href);

        // Handle relative URLs
        if (href.startsWith("//")) {
            URL url = URI.create(pageUrl).toURL();
            href = url.getProtocol() + ":" + href;
        } else if (href.startsWith("/")) {
            URL url = URI.create(pageUrl).toURL();
            href = url.getProtocol() + "://" + url.getHost() + href;
        }

        return href;
    }

    record HeadResponse(String contentType, String etag) {
        public static HeadResponse of(String contentType, String etag) {
            return new HeadResponse(Objects.requireNonNull(contentType, "text/html"), Objects.requireNonNull(etag, ""));
        }
    }

    /**
     * Determine content type of a URL.
     *
     * @param url the URL
     * @return the content type
     * @throws IOException if an I/O error occurs
     */
    private HeadResponse sendHeadRequest(String url) throws IOException, URISyntaxException {
        HttpURLConnection connection = (HttpURLConnection) new URI(url).toURL().openConnection();
        connection.setRequestMethod("HEAD");
        connection.connect();
        var contentType = connection.getContentType();
        var etag = connection.getHeaderField("Etag");
        connection.disconnect();

        return new HeadResponse(contentType, etag);
    }

    /**
     * Process a PDF menu.
     *
     * @param pdfUrl the PDF URL
     * @return the processed menu content as a base64 image
     * @throws IOException if an I/O error occurs
     */
    private String processPdfMenu(String pdfUrl) throws IOException, URISyntaxException {
        // TODO: Check if pdf size is under certain file size

        try (var inputStream = new URI(pdfUrl).toURL().openStream()) {
            byte[] pdfBytes = inputStream.readAllBytes();
            return Base64.getEncoder().encodeToString(pdfBytes);
        }
    }

    /**
     * Process an image menu.
     *
     * @param imageUrl the image URL
     * @return the processed menu content as a base64 image
     * @throws IOException if an I/O error occurs
     */
    private String processImageMenu(String imageUrl) throws IOException {
        var originalImage = ImageIO.read(URI.create(imageUrl).toURL());
        var resizedImage = resizeImage(originalImage, 1000);

        LOGGER.info("Converting image to base64 for OpenAI analysis");
        return imageToBase64(resizedImage);
    }

    /**
     * Resize an image to a maximum dimension.
     *
     * @param originalImage the original image
     * @param maxDimension the maximum dimension
     * @return the resized image
     */
    public static BufferedImage resizeImage(BufferedImage originalImage, int maxDimension) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        if (width <= maxDimension && height <= maxDimension) {
            return originalImage;
        }

        if (width > height) {
            return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_WIDTH, maxDimension, maxDimension);
        } else {
            return Scalr.resize(originalImage, Scalr.Method.QUALITY, Scalr.Mode.FIT_TO_HEIGHT, maxDimension, maxDimension);
        }
    }

    /**
     * Convert an image to base64.
     *
     * @param image the image
     * @return the base64 string
     * @throws IOException if an I/O error occurs
     */
    public static String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Calculate hash for menu content.
     *
     * @param content the content
     * @return the hash
     */
    private String calculateHash(String content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(content.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error calculating hash", e);
        }
    }

    /**
     * Process pending checks.
     */
    private void processPendingChecks() {
        List<BarCheck> pendingChecks = barCheckService.getChecksByStatus(BarCheck.ProcessingStatus.PENDING);
        LOGGER.info("Found {} pending checks to process", pendingChecks.size());

        for (BarCheck check : pendingChecks) {
            try {
                barCheckService.processCheck(check.getId());
            } catch (Exception e) {
                LOGGER.error("Error processing check {}: {}", check.getId(), e.getMessage(), e);
            }
        }
    }
}
