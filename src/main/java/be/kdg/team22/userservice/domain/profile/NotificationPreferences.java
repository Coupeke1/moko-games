package be.kdg.team22.userservice.domain.profile;

public record NotificationPreferences(
        boolean receivePlatform,
        boolean receiveEmail,
        boolean social,
        boolean game,
        boolean achievement,
        boolean commerce,
        boolean chat
) {
}