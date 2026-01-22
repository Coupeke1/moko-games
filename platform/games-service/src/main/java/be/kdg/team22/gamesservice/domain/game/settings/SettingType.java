package be.kdg.team22.gamesservice.domain.game.settings;

import org.jmolecules.ddd.annotation.ValueObject;

@ValueObject
public enum SettingType {
    STRING,
    INTEGER,
    BOOLEAN,
    ENUM
}