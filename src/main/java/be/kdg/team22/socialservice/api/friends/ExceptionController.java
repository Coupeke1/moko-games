package be.kdg.team22.socialservice.api.friends;

import be.kdg.team22.socialservice.domain.friendship.exceptions.FriendshipNotFoundException;
import be.kdg.team22.socialservice.domain.friendship.exceptions.NoMatchingUsersException;
import be.kdg.team22.socialservice.domain.user.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler({FriendshipNotFoundException.class, NoMatchingUsersException.class, UserNotFoundException.class})
    public ResponseEntity<String> handleNotFound(final RuntimeException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknown(final Exception exception) {
        return new ResponseEntity<>("Internal server error: " + exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}