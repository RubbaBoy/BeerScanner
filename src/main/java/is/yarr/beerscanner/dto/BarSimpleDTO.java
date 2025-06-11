package is.yarr.beerscanner.dto;

import java.util.Objects;

public class BarSimpleDTO {
    private Long id;
    private String name;

    public BarSimpleDTO() {
    }

    public BarSimpleDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        BarSimpleDTO that = (BarSimpleDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
