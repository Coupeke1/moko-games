package be.kdg.team22.userservice.api;

import be.kdg.team22.userservice.domain.library.exceptions.LibraryException;
import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import be.kdg.team22.userservice.domain.profile.exceptions.NotAuthenticatedException;
import be.kdg.team22.userservice.domain.profile.exceptions.ProfileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({ClaimNotFoundException.class, ProfileNotFoundException.class})
    public ResponseEntity<String> handleNotFound(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotAuthenticatedException.class)
    public ResponseEntity<String> handleNotAuthenticated(NotAuthenticatedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(LibraryException.class)
    public ResponseEntity<String> handleLibraryErrors(LibraryException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnknown(Exception ex) {
        return new ResponseEntity<>("Internal server error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}