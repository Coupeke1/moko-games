package be.kdg.team22.userservice.infrastructure.profile.jpa;

import be.kdg.team22.userservice.domain.profile.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profiles")
public class ProfileEntity {
    @Id
    @Column(name = "keycloak_id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image", nullable = false)
    private String image;

    @Embedded
    private StatisticsEmbed statistics;

    @Embedded
    private ModulesEmbed modules;

    @Embedded
    private NotificationsEmbed notifications;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected ProfileEntity() {
    }

    public ProfileEntity(final UUID id, final String username, final String email, final String description, final String image, final StatisticsEmbed statistics, final ModulesEmbed modules, final NotificationsEmbed notifications, final Instant createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.description = description;
        this.image = image;
        this.statistics = statistics;
        this.modules = modules;
        this.notifications = notifications;
        this.createdAt = createdAt;
    }

    public static ProfileEntity from(final Profile profile) {
        StatisticsEmbed statistics = new StatisticsEmbed(profile.statistics().level(), profile.statistics().playTime());

        ModulesEmbed modules = new ModulesEmbed(profile.modules().achievements(), profile.modules().favourites());

        Notifications notifications = profile.notifications();
        return new ProfileEntity(profile.id().value(), profile.username().value(), profile.email().value(), profile.description(), profile.image(), statistics, modules, new NotificationsEmbed(notifications.receiveEmail(), notifications.social(), notifications.achievements(), notifications.commerce(), notifications.chat()), profile.createdAt());
    }

    public Profile to() {
        Statistics statistics = new Statistics(this.statistics.level(), this.statistics.playTime());

        Modules modules = new Modules(this.modules.achievements(), this.modules.favourites());

        Notifications notifications = new Notifications(this.notifications.receiveEmail, this.notifications.social, this.notifications.achievements, this.notifications.commerce, this.notifications.chat);
        return new Profile(new ProfileId(id), new ProfileName(username), new ProfileEmail(email), description, image, statistics, modules, notifications, createdAt);
    }
}