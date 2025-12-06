package be.kdg.team22.communicationservice.api;

import be.kdg.team22.communicationservice.domain.chat.exceptions.*;
import be.kdg.team22.communicationservice.domain.notification.exceptions.ClaimNotFoundException;
import be.kdg.team22.communicationservice.domain.notification.exceptions.NotificationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(NotInLobbyException.class)
    public ResponseEntity<String> handleNotInLobby(final NotInLobbyException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ChatChannelNotFoundException.class, NotificationNotFoundException.class})
    public ResponseEntity<String> handleNotFound(final Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MessageEmptyException.class, CantAutoCreateBotChannel.class, BotMessageInLobbyException.class, ClaimNotFoundException.class})
    public ResponseEntity<String> handleEmptyMessage(final Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(final RuntimeException ex) {
        return new ResponseEntity<>("Internal error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}