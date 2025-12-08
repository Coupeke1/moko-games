package be.kdg.team22.userservice.infrastructure.profile.jpa;

import jakarta.persistence.Embeddable;

@Embeddable
public class NotificationPreferencesEmbed {
    public boolean receiveEmail;
    public boolean social;
    public boolean achievement;
    public boolean commerce;
    public boolean chat;

    protected NotificationPreferencesEmbed() {
    }

    public NotificationPreferencesEmbed(final boolean receiveEmail,
                                        final boolean social, final boolean achievement,
                                        final boolean commerce, final boolean chat) {
        this.receiveEmail = receiveEmail;
        this.social = social;
        this.achievement = achievement;
        this.commerce = commerce;
        this.chat = chat;
    }
}