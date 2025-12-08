package be.kdg.team22.storeservice.domain.catalog;

import org.jmolecules.ddd.annotation.Entity;

@Entity
public record Post(PostId id, String title, String image,
                   PostType type,
                   String content) {}
