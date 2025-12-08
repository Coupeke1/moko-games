package be.kdg.team22.storeservice.domain.catalog;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum PostType {
    INFO, MINOR_UPDATE, MAJOR_UPDATE, DEVLOG
}
