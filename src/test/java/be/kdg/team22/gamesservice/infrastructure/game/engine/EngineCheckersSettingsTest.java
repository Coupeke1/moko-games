package be.kdg.team22.gamesservice.infrastructure.game.engine;

import org.junit.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EngineCheckersSettingsTest {
    @Test
    public void recordStoresValues() {
        EngineCheckersSettings s = new EngineCheckersSettings(8, true);
        assertThat(s.boardSize()).isEqualTo(8);
        assertThat(s.flyingKings()).isTrue();
    }
}
