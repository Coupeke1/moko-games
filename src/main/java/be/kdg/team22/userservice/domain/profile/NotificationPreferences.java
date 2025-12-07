package be.kdg.team22.userservice.domain.profile;

public record NotificationPreferences(
        boolean receiveEmail,
        boolean social,
        boolean achievement,
        boolean commerce,
        boolean chat
) {
}