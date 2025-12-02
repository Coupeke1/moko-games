package be.kdg.team22.userservice.domain.library.exceptions;

public class LibraryException extends RuntimeException {

    private LibraryException(String message) {
        super(message);
    }

    public static LibraryException missingUserId() {
        return new LibraryException("userId cannot be null");
    }

    public static LibraryException missingGameId() {
        return new LibraryException("gameId cannot be null");
    }

    public static LibraryException missingPurchasedAt() {
        return new LibraryException("purchasedAt cannot be null");
    }

    public static LibraryException notInLibrary() {
        return new LibraryException("This game is not in your library");
    }
}
