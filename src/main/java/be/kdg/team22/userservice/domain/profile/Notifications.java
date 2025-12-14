package be.kdg.team22.userservice.domain.profile;

public record Notifications(
        boolean receiveEmail,
        boolean social,
        boolean achievements,
        boolean commerce,
        boolean chat
) {
}