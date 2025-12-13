package be.kdg.team22.gamesservice.infrastructure.game.jpa;

import be.kdg.team22.gamesservice.domain.game.Achievement;
import be.kdg.team22.gamesservice.domain.game.AchievementKey;
import jakarta.persistence.*;

@Entity
@Table(name = "achievements")
public class AchievementEntity {
    @EmbeddedId
    private AchievementId id;

    @MapsId("gameId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private GameEntity game;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "levels")
    private int levels;

    protected AchievementEntity() {
    }

    public AchievementEntity(final String key, final GameEntity game, final String name, final String description, final int levels) {
        this.id = new AchievementId(key, game.id());
        this.game = game;
        this.name = name;
        this.description = description;
        this.levels = levels;
    }

    public static AchievementEntity fromDomain(final Achievement achievement, final GameEntity gameEntity) {
        return new AchievementEntity(
                achievement.key().key(),
                gameEntity,
                achievement.name(),
                achievement.description(),
                achievement.levels()
        );
    }

    public Achievement toDomain() {
        return new Achievement(
                new AchievementKey(this.id.key()),
                this.name,
                this.description,
                this.levels
        );
    }

    public void fromDomainUpdate(final Achievement achievement) {
        this.name = achievement.name();
        this.description = achievement.description();
        this.levels = achievement.levels();
    }

    public AchievementId id() {
        return id;
    }

    public GameEntity game() {
        return game;
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
}
