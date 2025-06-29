package is.yarr.beerscanner.model.beer;


import is.yarr.beerscanner.model.Bar;
import is.yarr.beerscanner.model.Beer;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "bar_beers_current",
        uniqueConstraints = @UniqueConstraint(columnNames = {"bar_id", "beer_id"}))
public class BarBeerCurrent {

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

    @Column(name = "last_verified_at")
    private LocalDateTime lastVerifiedAt;

    public BarBeerCurrent() {
    }

    public BarBeerCurrent(Bar bar, Beer beer) {
        this.addedAt = LocalDateTime.now();
        this.lastVerifiedAt = LocalDateTime.now();
        this.bar = bar;
        this.beer = beer;
    }

    public BarBeerCurrent(Bar bar, Beer beer, LocalDateTime addedAt, LocalDateTime lastVerifiedAt) {
        this.bar = bar;
        this.beer = beer;
        this.addedAt = addedAt != null ? addedAt : LocalDateTime.now();
        this.lastVerifiedAt = lastVerifiedAt != null ? lastVerifiedAt : LocalDateTime.now();
    }

    public LocalDateTime getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public Beer getBeer() {
        return beer;
    }

    public void setBeer(Beer beer) {
        this.beer = beer;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public Long getId() {
        return id;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof BarBeerCurrent that)) return false;

        return Objects.equals(bar, that.bar) && Objects.equals(beer, that.beer);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(bar);
        result = 31 * result + Objects.hashCode(beer);
        return result;
    }
}
