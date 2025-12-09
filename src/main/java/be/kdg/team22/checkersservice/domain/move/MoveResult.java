package be.kdg.team22.checkersservice.domain.move;

public record MoveResult(
        boolean capture,
        boolean multiCapture,
        boolean promotion,
        int kingCount
) {
}
