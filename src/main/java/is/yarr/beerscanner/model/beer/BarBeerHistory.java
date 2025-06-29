package is.yarr.beerscanner.model.beer;

import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.Beer;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bar_beers_history")
public class BarBeerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bar_id", nullable = false)
    private Bar bar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beer_id", nullable = false)
    private Beer beer;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @Column(name = "removed_at", nullable = false)
    private LocalDateTime removedAt;

    public BarBeerHistory() {
    }

    public BarBeerHistory(Bar bar, Beer beer, LocalDateTime addedAt, LocalDateTime removedAt) {
        this.bar = bar;
        this.beer = beer;
        this.addedAt = addedAt;
        this.removedAt = removedAt;
    }

    public Long getId() {
        return id;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public Beer getBeer() {
        return beer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public LocalDateTime getRemovedAt() {
        return removedAt;
    }

    public void setRemovedAt(LocalDateTime removedAt) {
        this.removedAt = removedAt;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BarBeerHistory that)) return false;

        return Objects.equals(bar, that.bar) && Objects.equals(beer, that.beer);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(bar);
        result = 31 * result + Objects.hashCode(beer);
        return result;
    }
}
