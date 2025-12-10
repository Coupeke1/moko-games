package be.kdg.team22.userservice.domain.profile;

import be.kdg.team22.userservice.domain.profile.exceptions.CannotUpdateProfileException;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;

@AggregateRoot
public class Profile {
    private final ProfileId id;
    private final ProfileName username;
    private final ProfileEmail email;
    private Statistics statistics;
    private final Instant createdAt;
    private NotificationPreferences preferences;
    private String description;
    private String image;
    private Modules modules;

    public Profile(final ProfileId id, final ProfileName username, final ProfileEmail email, final String description, final String image, final Statistics statistics, final Modules modules, final Instant createdAt, final NotificationPreferences preferences) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.description = description;
        this.image = image;
        this.statistics = statistics;
        this.modules = modules;
        this.createdAt = createdAt;
        this.preferences = preferences;
    }

    public Profile(final ProfileId id, final ProfileName username, final ProfileEmail email, final String description, final String image) {
        this(id, username, email, description, image, new Statistics(0, 0), new Modules(false, false), Instant.now(), new NotificationPreferences(true, true, true, true, true));
    }

    public void updateDescription(final String description) {
        if (this.description.equals(description))
            throw CannotUpdateProfileException.description(id);

        this.description = description;
    }

    public void updateImage(final String image) {
        if (this.image.equals(image))
            throw CannotUpdateProfileException.image(id);

        this.image = image;
    }

    public void updateModules(final Modules modules) {
        if (this.modules.equals(modules))
            throw CannotUpdateProfileException.modules(id);

        this.modules = modules;
    }

    public void updatePreferences(final NotificationPreferences prefs) {
        if (this.preferences.equals(prefs))
            throw CannotUpdateProfileException.preferences(id);

        this.preferences = prefs;
    }

    public void addLevels(final int amount) {
        if (amount < 0) {
            throw CannotUpdateProfileException.levels(id);
        }
        this.statistics = new Statistics(this.statistics.level() + amount, this.statistics.playTime());
    }

    public NotificationPreferences preferences() {
        return preferences;
    }

    public ProfileId id() {
        return id;
    }

    public ProfileName username() {
        return username;
    }

    public ProfileEmail email() {
        return email;
    }

    public String description() {
        return description;
    }

    public String image() {
        return image;
    }

    public Statistics statistics() {
        return statistics;
    }

    public Modules modules() {
        return modules;
    }

    public Instant createdAt() {
        return createdAt;
    }
}