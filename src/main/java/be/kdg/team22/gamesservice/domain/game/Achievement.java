package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.domain.game.exceptions.AchievementDescriptionInvalidException;
import be.kdg.team22.gamesservice.domain.game.exceptions.AchievementLevelsInvalidException;
import be.kdg.team22.gamesservice.domain.game.exceptions.AchievementNameInvalidException;
import org.jmolecules.ddd.annotation.Entity;

import java.util.Objects;

@Entity
public class Achievement {
    private final AchievementKey key;
    private String name;
    private String description;
    private int levels;

    public Achievement(final AchievementKey key, final String name, final String description, final int levels) {
        validate(name, description, levels);
        this.key = key;
        this.name = name;
        this.description = description;
        this.levels = levels;
    }

    private void validate(final String name, final String description, final int levels) {
        if (name == null || name.isEmpty()) throw new AchievementNameInvalidException();
        if (description == null || description.isEmpty()) throw new AchievementDescriptionInvalidException();
        if (levels < 0) throw new AchievementLevelsInvalidException();
    }

    public void update(final String name, final String description, final int levels) {
        validate(name, description, levels);
        this.name = name;
        this.description = description;
        this.levels = levels;
    }

    public AchievementKey key() {
        return key;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public int levels() {
        return levels;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Achievement that = (Achievement) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key);
    }
}
