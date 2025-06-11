package is.yarr.beerscanner.service.openai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public class BeerListOutput {

    public List<BeerOutput> beers;

    public static class BeerOutput {
        @JsonPropertyDescription("The name of the beer")
        public String name;

        @JsonPropertyDescription("The name of the brewery that produces the beer")
        public String brewery;

        @JsonPropertyDescription("The type or style of the beer")
        public String type;

        @JsonPropertyDescription("A description of the beer provided by the menu")
        public String description;

        @JsonPropertyDescription("The alcohol by volume (ABV) percentage of the beer")
        public double abv;

        @Override
        public String toString() {
            return "BeerOutput{name='%s', brewery='%s', type='%s', description='%s', abv=%s}".formatted(name, brewery, type, description, abv);
        }
    }

    @Override
    public String toString() {
        return "BeerListOutput{beers=%s}".formatted(beers);
    }
}
