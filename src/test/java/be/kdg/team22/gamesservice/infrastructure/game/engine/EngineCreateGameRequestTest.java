package be.kdg.team22.gamesservice.infrastructure.game.engine;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EngineCreateGameRequestTest {

    @Test
    void recordStoresValues() {
        var players = List.of(UUID.randomUUID());
        var settings = Map.<String, Object>of(
                "boardSize", 3,
                "flyingKings", true
        );

        var req = new EngineCreateGameRequest(players, settings, true);

        assertThat(req.players()).isEqualTo(players);
        assertThat(req.settings()).isEqualTo(settings);
        assertThat(req.aiPlayer()).isTrue();
    }
}