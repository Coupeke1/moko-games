package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.domain.game.exceptions.InvalidGameConfigurationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

    private GameId gid() {
        return GameId.from(UUID.randomUUID());
    }

    @Test
    void constructor_createsValidGame() {
        GameId id = gid();

        Game game = new Game(id, "checkers", "http://localhost:9092", "/start");

        assertThat(game.id()).isEqualTo(id);
        assertThat(game.name()).isEqualTo("checkers");
        assertThat(game.baseUrl()).isEqualTo("http://localhost:9092");
        assertThat(game.startEndpoint()).isEqualTo("/start");
    }

    @Test
    void constructor_nullIdThrows() {
        assertThatThrownBy(() ->
                new Game(null, "checkers", "http://localhost:9092", "/start")
        ).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("GameId");
    }

    @Test
    void constructor_blankNameThrows() {
        assertThatThrownBy(() ->
                new Game(gid(), "  ", "http://localhost:9092", "/start")
        ).isInstanceOf(InvalidGameConfigurationException.class)
                .hasMessageContaining("Game name cannot be empty");
    }

    @Test
    void constructor_blankBaseUrlThrows() {
        assertThatThrownBy(() ->
                new Game(gid(), "checkers", " ", "/start")
        ).isInstanceOf(InvalidGameConfigurationException.class)
                .hasMessageContaining("BaseUrl cannot be empty");
    }

    @Test
    void constructor_blankStartEndpointThrows() {
        assertThatThrownBy(() ->
                new Game(gid(), "checkers", "http://localhost:9092", " ")
        ).isInstanceOf(InvalidGameConfigurationException.class)
                .hasMessageContaining("Start endpoint cannot be empty");
    }
}
