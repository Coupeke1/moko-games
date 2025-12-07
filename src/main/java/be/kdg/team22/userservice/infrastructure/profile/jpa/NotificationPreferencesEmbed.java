package be.kdg.team22.userservice.infrastructure.profile.jpa;

import jakarta.persistence.Embeddable;

@Embeddable
public class NotificationPreferencesEmbed {
    public boolean receivePlatform;
    public boolean receiveEmail;

    public boolean social;
    public boolean game;
    public boolean achievement;
    public boolean commerce;
    public boolean chat;

    protected NotificationPreferencesEmbed() {
    }

    public NotificationPreferencesEmbed(final boolean receivePlatform, final boolean receiveEmail,
                                        final boolean social, final boolean game, final boolean achievement,
                                        final boolean commerce, final boolean chat) {
        this.receivePlatform = receivePlatform;
        this.receiveEmail = receiveEmail;
        this.social = social;
        this.game = game;
        this.achievement = achievement;
        this.commerce = commerce;
        this.chat = chat;
    }
}