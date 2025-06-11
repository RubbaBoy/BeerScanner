package is.yarr.beerscanner.beerscanner;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = "is.yarr.beerscanner")
@EntityScan(basePackages = "is.yarr.beerscanner.model")
@EnableJpaRepositories(basePackages = "is.yarr.beerscanner.repository")
public class BeerScannerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeerScannerApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @PostConstruct
    public void initialize() throws IOException {
        // This method can be used for any initialization logic if needed
        System.out.println("BeerScannerApplication has started successfully.");

            // Parse HTML with JSoup
            Document doc = Jsoup.connect("https://octopusbrew.com/buffalo-spotted-octopus-brewing-co-buffalo-ny-drink-menu").get();
//            Elements elements = doc.selectXpath("//*[@id=\"drink_menu_v2\"]/div/div/div[1]/div[2]/section");
//
//            if (elements.isEmpty() || elements.first() == null) {
//                throw new IOException("No element found with XPath: ");
//            }

//            String cleanedHtml = elements.first().html();
//        var cleanedHtml = doc.html();

            // Convert to PDF using iText
//            HtmlConverter.convertToPdf(cleanedHtml, new FileOutputStream(Paths.get("/e/BeerScanner/bar-menu2.pdf").toFile()));

        // Convert JSoup document to W3C Document
        W3CDom w3cDom = new W3CDom();
        org.w3c.dom.Document w3cDoc = w3cDom.fromJsoup(doc);

        // Generate PDF
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(w3cDoc, "https://octopusbrew.com/buffalo-spotted-octopus-brewing-co-buffalo-ny-drink-menu");
        renderer.layout();

        try (FileOutputStream fos = new FileOutputStream("/e/BeerScanner/bar-menu2.pdf")) {
            renderer.createPDF(fos);
        }


        System.out.println("Done");


    }
}
