package be.kdg.team22.storeservice.infrastructure.catalog.jpa;

import be.kdg.team22.storeservice.domain.catalog.Entry;
import be.kdg.team22.storeservice.domain.catalog.GameCategory;
import be.kdg.team22.storeservice.domain.catalog.GameId;
import be.kdg.team22.storeservice.domain.catalog.Post;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "store_catalog")
public class EntryEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameCategory category;

    @Column(nullable = false, name = "purchase_count")
    private int purchaseCount;

    @OneToMany(mappedBy = "entry", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Collection<PostEntity> posts = new ArrayList<>();

    protected EntryEntity() {}

    public EntryEntity(final UUID id, final BigDecimal price, final GameCategory category, final int purchaseCount, final Collection<PostEntity> posts) {
        this.id = id;
        this.price = price;
        this.category = category;
        this.purchaseCount = purchaseCount;
        this.posts = posts;
    }

    public static EntryEntity from(final Entry entry) {
        List<PostEntity> posts = entry.posts().stream().map(PostEntity::from).toList();

        return new EntryEntity(entry.id().value(), entry.price(), entry.category(), entry.purchaseCount(), posts);
    }

    public Entry to() {
        return new Entry(GameId.from(this.id), this.price, this.category, this.purchaseCount, Collections.emptyList());
    }

    public Entry toWithPosts() {
        List<Post> posts = this.posts.stream().map(PostEntity::to).toList();
        return new Entry(GameId.from(this.id), this.price, this.category, this.purchaseCount, posts);
    }

    public UUID id() {
        return id;
    }

    public BigDecimal price() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public GameCategory category() {
        return category;
    }

    public void setCategory(final GameCategory category) {
        this.category = category;
    }

    public int purchaseCount() {
        return purchaseCount;
    }

    public void setPurchaseCount(final int purchaseCount) {
        this.purchaseCount = purchaseCount;
    }
}