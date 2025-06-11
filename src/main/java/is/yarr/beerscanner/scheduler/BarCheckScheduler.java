package is.yarr.beerscanner.scheduler;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.BarCheck;
import is.yarr.beerscanner.service.BarCheckService;
import is.yarr.beerscanner.service.BarService;
import is.yarr.beerscanner.service.NotificationService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
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
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public BarCheckScheduler(BarService barService, BarCheckService barCheckService, NotificationService notificationService) {
        this.barService = barService;
        this.barCheckService = barCheckService;
        this.notificationService = notificationService;
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

        // If XPath is provided, navigate to the page and extract the actual menu URL
        if (bar.getMenuXPath() != null && !bar.getMenuXPath().isEmpty()) {
            LOGGER.info("Using XPath to extract menu URL from {}", finalMenuUrl);
            finalMenuUrl = extractMenuUrlUsingXPath(finalMenuUrl, bar.getMenuXPath());
            LOGGER.info("Extracted menu URL: {}", finalMenuUrl);
        }

        // Determine content type
        HeadResponse headResponse = sendHeadRequest(finalMenuUrl);
        LOGGER.info("Content type for {}: {}", finalMenuUrl, headResponse.contentType);
        LOGGER.info("Etag for {}: {}", finalMenuUrl, headResponse.etag);
        // TODO: Handle etag

        String base64;
        if (headResponse.contentType.contains("application/pdf")) {
            // Handle PDF
            LOGGER.info("Processing PDF menu from {}", finalMenuUrl);
            base64 = processPdfMenu(finalMenuUrl);
        } else if (headResponse.contentType.contains("image/")) {
            // Handle image
            LOGGER.info("Processing image menu from {}", finalMenuUrl);
            base64 = processImageMenu(finalMenuUrl);
        } else {
            throw new RuntimeException("Unsupported content type: " + headResponse.contentType);
        }

        return new MenuContent(base64, headResponse.contentType);

//        else {
//            // Handle text (current implementation)
//            LOGGER.info("Processing text menu from {}", finalMenuUrl);
//            return fetchTextContent(finalMenuUrl);
//        }
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
            URL url = new URL(pageUrl);
            href = url.getProtocol() + ":" + href;
        } else if (href.startsWith("/")) {
            URL url = new URL(pageUrl);
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
        // Download image
        BufferedImage originalImage = ImageIO.read(new URL(imageUrl));

        // Resize image to max 1000px
        BufferedImage resizedImage = resizeImage(originalImage, 1000);

        // Convert to Base64 for OpenAI
        // Note: The current OpenAI Java library might not fully support the vision API
        // In a production environment, you would need to use a library that supports the vision API
        // or make a direct HTTP request to the OpenAI API
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
    private BufferedImage resizeImage(BufferedImage originalImage, int maxDimension) {
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
    private String imageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Fetch text content from a URL.
     *
     * @param url the URL
     * @return the text content
     * @throws IOException if an I/O error occurs
     */
    private String fetchTextContent(String url) throws IOException, URISyntaxException {
        HttpURLConnection connection = (HttpURLConnection) new URI(url).toURL().openConnection();
        connection.setRequestMethod("GET");

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }

        return content.toString();
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
