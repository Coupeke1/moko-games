package be.kdg.team22.userservice.domain.library.exceptions;

import be.kdg.team22.userservice.domain.library.GameId;
import be.kdg.team22.userservice.domain.profile.ProfileEmail;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.domain.profile.ProfileName;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(GameId id) {
        super(String.format("Game with id '%s' was not found", id));
    }
}