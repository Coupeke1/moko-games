package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.domain.game.exceptions.GameBaseUrlInvalidException;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameIdNullException;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameNameInvalidException;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameStartEndpointInvalidException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
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
        assertThat(game.createdAt()).isNotNull();
        assertThat(game.updatedAt()).isEqualTo(game.createdAt());
    }

    @Test
    void fullConstructor_restoresStateCorrectly() {
        GameId id = gid();
        Instant created = Instant.parse("2024-01-01T10:00:00Z");
        Instant updated = Instant.parse("2024-01-02T12:00:00Z");

        Game game = new Game(
                id,
                "checkers",
                "http://localhost:9092",
                "/start",
                created,
                updated
        );

        assertThat(game.createdAt()).isEqualTo(created);
        assertThat(game.updatedAt()).isEqualTo(updated);
    }

    @Test
    void constructor_nullIdThrows() {
        assertThatThrownBy(() ->
                new Game(null, "checkers", "http://localhost:9092", "/start")
        ).isInstanceOf(GameIdNullException.class);
    }

    @Test
    void constructor_blankNameThrows() {
        assertThatThrownBy(() ->
                new Game(gid(), " ", "http://localhost:9092", "/start")
        ).isInstanceOf(GameNameInvalidException.class);
    }

    @Test
    void constructor_blankBaseUrlThrows() {
        assertThatThrownBy(() ->
                new Game(gid(), "checkers", " ", "/start")
        ).isInstanceOf(GameBaseUrlInvalidException.class);
    }

    @Test
    void constructor_blankStartEndpointThrows() {
        assertThatThrownBy(() ->
                new Game(gid(), "checkers", "http://localhost:9092", " ")
        ).isInstanceOf(GameStartEndpointInvalidException.class);
    }

    @Test
    void rename_changesNameAndUpdatesTimestamp() {
        Game game = new Game(gid(), "old", "http://x", "/start");

        Instant before = game.updatedAt();
        game.rename("new");

        assertThat(game.name()).isEqualTo("new");
        assertThat(game.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void rename_blankThrows() {
        Game game = new Game(gid(), "old", "http://x", "/start");

        assertThatThrownBy(() -> game.rename(" "))
                .isInstanceOf(GameNameInvalidException.class);
    }

    @Test
    void changeBaseUrl_changesUrlAndUpdatesTimestamp() {
        Game game = new Game(gid(), "name", "http://old", "/start");

        Instant before = game.updatedAt();
        game.changeBaseUrl("http://new");

        assertThat(game.baseUrl()).isEqualTo("http://new");
        assertThat(game.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void changeBaseUrl_blankThrows() {
        Game game = new Game(gid(), "name", "http://old", "/start");

        assertThatThrownBy(() -> game.changeBaseUrl(" "))
                .isInstanceOf(GameBaseUrlInvalidException.class);
    }

    @Test
    void changeStartEndpoint_changesEndpointAndUpdatesTimestamp() {
        Game game = new Game(gid(), "name", "http://old", "/start");

        Instant before = game.updatedAt();
        game.changeStartEndpoint("/play");

        assertThat(game.startEndpoint()).isEqualTo("/play");
        assertThat(game.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void changeStartEndpoint_blankThrows() {
        Game game = new Game(gid(), "name", "http://old", "/start");

        assertThatThrownBy(() -> game.changeStartEndpoint(" "))
                .isInstanceOf(GameStartEndpointInvalidException.class);
    }

    @Test
    void updatedAt_doesNotChangeIfNoMutations() {
        Game game = new Game(gid(), "name", "http://x", "/start");

        Instant updated = game.updatedAt();

        assertThat(game.updatedAt()).isEqualTo(updated);
    }
}