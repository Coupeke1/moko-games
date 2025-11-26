package be.kdg.team22.gamesservice.domain.game.exceptions;

public class GameMetadataException extends RuntimeException {

    private GameMetadataException(String message) {
        super(message);
    }

    public static GameMetadataException invalidTitle() {
        return new GameMetadataException("Game title cannot be null or blank");
    }

    public static GameMetadataException invalidDescription() {
        return new GameMetadataException("Game description cannot be null or blank");
    }

    public static GameMetadataException invalidPrice() {
        return new GameMetadataException("Game price cannot be negative or null");
    }

    public static GameMetadataException invalidImageUrl() {
        return new GameMetadataException("Game imageUrl cannot be null or blank");
    }

    public static GameMetadataException invalidStoreUrl() {
        return new GameMetadataException("Game storeUrl cannot be null or blank");
    }
}
