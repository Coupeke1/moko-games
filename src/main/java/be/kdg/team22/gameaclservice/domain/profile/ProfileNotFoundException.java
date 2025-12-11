package be.kdg.team22.gameaclservice.domain.profile;

public class ProfileNotFoundException extends RuntimeException {
    public ProfileNotFoundException(ProfileId id) {
        super(String.format("Profile with id '%s' was not found", id));
    }

}
