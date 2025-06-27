package is.yarr.beerscanner.dto;

/**
 * DTO for modifying Beer entity information.
 */
public class BeerModifyDTO {
    private String name;
    private String type;
    private String brewery;
    private Double abv;
    private String description;

    public BeerModifyDTO() {
    }

    public BeerModifyDTO(String name, String type, String brewery, Double abv, String description) {
        this.name = name;
        this.type = type;
        this.brewery = brewery;
        this.abv = abv;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBrewery() {
        return brewery;
    }

    public void setBrewery(String brewery) {
        this.brewery = brewery;
    }

    public Double getAbv() {
        return abv;
    }

    public void setAbv(Double abv) {
        this.abv = abv;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static BeerModifyDTOBuilder builder() {
        return new BeerModifyDTOBuilder();
    }

    public static class BeerModifyDTOBuilder {
        private String name;
        private String type;
        private String brewery;
        private Double abv;
        private String description;

        public BeerModifyDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BeerModifyDTOBuilder type(String type) {
            this.type = type;
            return this;
        }

        public BeerModifyDTOBuilder brewery(String brewery) {
            this.brewery = brewery;
            return this;
        }

        public BeerModifyDTOBuilder abv(Double abv) {
            this.abv = abv;
            return this;
        }

        public BeerModifyDTOBuilder description(String description) {
            this.description = description;
            return this;
        }

        public BeerModifyDTO build() {
            return new BeerModifyDTO(name, type, brewery, abv, description);
        }
    }
}
