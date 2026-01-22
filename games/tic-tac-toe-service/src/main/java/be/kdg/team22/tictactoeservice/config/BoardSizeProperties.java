package be.kdg.team22.tictactoeservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BoardSizeProperties {
    @Value("${tic-tac-toe.board.min-size}")
    private int minSize;

    @Value("${tic-tac-toe.board.max-size}")
    private int maxSize;

    public int minSize() {
        return minSize;
    }

    public void setMinSize(final int minSize) {
        this.minSize = minSize;
    }

    public int maxSize() {
        return maxSize;
    }

    public void setMaxSize(final int maxSize) {
        this.maxSize = maxSize;
    }
}