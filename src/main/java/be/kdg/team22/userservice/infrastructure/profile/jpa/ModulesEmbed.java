package be.kdg.team22.userservice.infrastructure.profile.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ModulesEmbed {
    @Column(name = "achievements", nullable = false)
    private boolean achievements;

    @Column(name = "favourites", nullable = false)
    private boolean favourites;

    protected ModulesEmbed() {}

    public ModulesEmbed(final boolean achievements, final boolean favourites) {
        this.achievements = achievements;
        this.favourites = favourites;
    }

    public boolean achievements() {
        return achievements;
    }

    public boolean favourites() {
        return favourites;
    }
}