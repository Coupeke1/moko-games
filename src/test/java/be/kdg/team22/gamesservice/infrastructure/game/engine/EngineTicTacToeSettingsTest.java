package be.kdg.team22.gamesservice.infrastructure.game.engine;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EngineTicTacToeSettingsTest {
    @Test
    public void recordStoresValues() {
        EngineTicTacToeSettings s = new EngineTicTacToeSettings(5);
        assertThat(s.boardSize()).isEqualTo(5);
    }
}
