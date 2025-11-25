package be.kdg.team22.gamesservice.infrastructure.game.engine;

import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EngineCreateGameRequestTest {
    @Test
    public void recordStoresValues() {
        var players = List.of(UUID.randomUUID());
        var req = new EngineCreateGameRequest(players, "settings");

        assertThat(req.players()).isEqualTo(players);
        assertThat(req.settings()).isEqualTo("settings");
    }
}
