package is.yarr.beerscanner.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseInputFile;
import com.openai.models.responses.ResponseInputItem;
import is.yarr.beerscanner.service.openai.BeerListOutput;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for OpenAI API operations.
 */
@Service
@Slf4j
public class OpenAIService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIService.class);

    private final OpenAIClient client;

    public OpenAIService(@Value("${openai.api.key}") String apiKey) {
        this.client = OpenAIOkHttpClient.builder().apiKey(apiKey).build();
    }

    /**
     * Extract beers from menu content.
     *
     * @param menuBase64Pdf the base64 encoded PDF content of the bar menu
     * @param barInstructions additional instructions for the AI, specific to the bar
     * @return a list of beers
     */
    public List<BeerListOutput.BeerOutput> extractBeersFromMenu(String menuBase64Pdf, String contentType, String barInstructions) {
        try {
            if (barInstructions == null || barInstructions.isEmpty()) {
                barInstructions = "";
            }

            var extension = contentType.split("/")[1];

            System.out.println("Extracting beers from menu with content type: " + contentType + " and extension: " + extension);
            System.out.println("The size of the menu is: " + menuBase64Pdf.length());

            var inputFile = ResponseInputFile.builder()
                    .filename("bar-menu.%s".formatted(extension))
                    .fileData("data:%s;base64,%s".formatted(contentType, menuBase64Pdf))
                    .build();

            var messageInputItem = ResponseInputItem.ofMessage(ResponseInputItem.Message.builder()
                    .role(ResponseInputItem.Message.Role.USER)
                    .addInputTextContent("Extract the beers from the attached menu.")
                    .addContent(inputFile)
                    .build());

            var params = ResponseCreateParams.builder()
                    .model(ChatModel.GPT_4_1_MINI)
                    .maxOutputTokens(2048)
                    .text(BeerListOutput.class)
                    .instructions("""
                                    You are a helpful assistant that extracts beer information from bar menus and only returns JSON.
                                    Extract all draft beers and ciders from the menu and return them as a JSON array.
                                    Each beer should have the following properties: name, brewery, type, abv (as a decimal, e.g., 5.3 for 5.3%), description (if found, be sure this is ONLY the description).
                                    If any property is not available, use null.
                                    If the beer name and brewery name are 100% indistinguishable from each other, put it as just the beer name.
                                    Use only official beer types (e.g. IPA, Lager, DIPA).
                                    
                                    """ + barInstructions)
                    .inputOfResponse(List.of(messageInputItem))
                    .build();

            var beerListOutputs = client.responses().create(params).output().stream()
                    .flatMap(item -> item.message().stream())
                    .flatMap(message -> message.content().stream())
                    .flatMap(content -> content.outputText().stream())
                    .toList();

            if (beerListOutputs.size() != 1) {
                LOGGER.error("Expected exactly one beer list output, but found: {}", beerListOutputs.size());
            }

            return beerListOutputs.getFirst().beers;
        } catch (Exception e) {
            log.error("Error extracting beers from menu", e);
            throw new RuntimeException("Error extracting beers from menu: " + e.getMessage(), e);
        }
    }
}
