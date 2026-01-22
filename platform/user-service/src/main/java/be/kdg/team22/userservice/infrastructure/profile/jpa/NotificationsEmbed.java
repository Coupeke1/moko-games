package be.kdg.team22.userservice.infrastructure.profile.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class NotificationsEmbed {
    public boolean receiveEmail;
    @Column(name = "social_notifications", nullable = false)
    public boolean social;
    @Column(name = "achievements_notifications", nullable = false)
    public boolean achievements;
    @Column(name = "commerce_notifications", nullable = false)
    public boolean commerce;
    @Column(name = "chat_notifications", nullable = false)
    public boolean chat;

    protected NotificationsEmbed() {
    }

    public NotificationsEmbed(final boolean receiveEmail, final boolean social, final boolean achievements, final boolean commerce, final boolean chat) {
        this.receiveEmail = receiveEmail;
        this.social = social;
        this.achievements = achievements;
        this.commerce = commerce;
        this.chat = chat;
    }
}