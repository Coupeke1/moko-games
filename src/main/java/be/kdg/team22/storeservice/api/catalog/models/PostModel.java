package be.kdg.team22.storeservice.api.catalog.models;

import be.kdg.team22.storeservice.domain.catalog.Post;
import be.kdg.team22.storeservice.domain.catalog.PostType;

import java.util.UUID;

public record PostModel(UUID id, String title,
                        String image,
                        PostType type,
                        String content) {
    public static PostModel from(Post post) {
        return new PostModel(post.id().value(), post.title(), post.image(), post.type(), post.content());
    }
}