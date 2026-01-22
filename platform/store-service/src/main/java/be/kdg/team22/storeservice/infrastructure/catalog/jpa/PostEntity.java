package be.kdg.team22.storeservice.infrastructure.catalog.jpa;

import be.kdg.team22.storeservice.domain.catalog.Post;
import be.kdg.team22.storeservice.domain.catalog.PostId;
import be.kdg.team22.storeservice.domain.catalog.PostType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "posts")
public class PostEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PostType type;

    @Column(nullable = false, length = 3072)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entry_id", nullable = false)
    private EntryEntity entry;

    protected PostEntity() {}

    public PostEntity(final UUID id, final String title, final String image, final PostType type, final String content) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.content = content;
    }

    public static PostEntity from(final Post post) {
        return new PostEntity(post.id().value(), post.title(), post.image(), post.type(), post.content());
    }

    public Post to() {
        return new Post(PostId.from(id), title, image, type, content);
    }
}
