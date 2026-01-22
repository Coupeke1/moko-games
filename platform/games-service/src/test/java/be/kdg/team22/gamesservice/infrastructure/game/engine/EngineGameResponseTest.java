package be.kdg.team22.gamesservice.infrastructure.game.engine;

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EngineGameResponseTest {
    @Test
    public void recordStoresValues() {
        UUID id = UUID.randomUUID();
        EngineGameResponse r = new EngineGameResponse(id);
        assertThat(r.id()).isEqualTo(id);
    }
}
