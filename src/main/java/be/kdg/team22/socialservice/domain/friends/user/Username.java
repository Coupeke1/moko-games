package be.kdg.team22.socialservice.domain.friends.user;

import be.kdg.team22.socialservice.domain.friends.user.exceptions.InvalidUsernameException;
import be.kdg.team22.socialservice.domain.friends.user.exceptions.NotFoundException;

public record Username(String username) {
    public Username {
        if (username == null)
            throw new InvalidUsernameException();
    }

    public NotFoundException notFound() {
        return new NotFoundException(username);
    }

    @Override
    public String toString() {
        return username;
    }
}