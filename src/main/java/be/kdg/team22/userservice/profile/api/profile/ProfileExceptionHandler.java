package be.kdg.team22.userservice.profile.api.profile;

import be.kdg.team22.userservice.profile.application.profile.MissingRequiredClaimException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice(assignableTypes = ProfileController.class)
public class ProfileExceptionHandler {
    @ExceptionHandler(MissingRequiredClaimException.class)
    public ResponseEntity<?> missingClaim(MissingRequiredClaimException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notFound(NoSuchElementException ex) {
        return ResponseEntity.status(404).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validation(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(
                ex.getBindingResult().getFieldErrors().stream()
                        .map(err -> {
                            assert err.getDefaultMessage() != null;
                            return Map.of("field", err.getField(), "message", err.getDefaultMessage());
                        })
                        .toList()
        );
    }
}
