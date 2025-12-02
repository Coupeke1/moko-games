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

    public static GameMetadataException invalidImage() {
        return new GameMetadataException("Game image cannot be null or blank");
    }
}
