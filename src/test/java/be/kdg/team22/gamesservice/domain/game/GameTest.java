package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.domain.game.exceptions.*;
import be.kdg.team22.gamesservice.domain.game.settings.GameSettingsDefinition;
import be.kdg.team22.gamesservice.domain.game.settings.SettingDefinition;
import be.kdg.team22.gamesservice.domain.game.settings.SettingType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

    private GameId gid() {
        return GameId.from(UUID.randomUUID());
    }

    private Game createValidGame() {
        return new Game(
                gid(),
                "engine-name",
                "http://localhost",
                "http://frontend",
                "/start",
                "/health",
                "Title",
                "Description",
                "http://img",
                defaultSettings()
        );
    }

    private GameSettingsDefinition defaultSettings() {
        return new GameSettingsDefinition(
                List.of(
                        new SettingDefinition(
                                "dummy",
                                SettingType.STRING,
                                false,
                                null,
                                null,
                                null,
                                "dummy"
                        )
                )
        );
    }

    @Test
    void constructor_createsValidGame() {
        GameId id = gid();

        Game game = new Game(
                id,
                "engine",
                "http://x",
                "/start",
                "/start",
                "/health",
                "Title",
                "Desc",
                "http://img",
                defaultSettings()
        );

        assertThat(game.id()).isEqualTo(id);
        assertThat(game.name()).isEqualTo("engine");
        assertThat(game.baseUrl()).isEqualTo("http://x");
        assertThat(game.startEndpoint()).isEqualTo("/start");
        assertThat(game.healthEndpoint()).isEqualTo("/health");
        assertThat(game.title()).isEqualTo("Title");
        assertThat(game.description()).isEqualTo("Desc");
        assertThat(game.image()).isEqualTo("http://img");

        assertThat(game.createdAt()).isNotNull();
        assertThat(game.updatedAt()).isEqualTo(game.createdAt());
    }

    @Test
    void fullConstructor_restoresState() {
        GameId id = gid();
        Instant created = Instant.parse("2024-01-01T10:00:00Z");
        Instant updated = Instant.parse("2024-01-02T10:00:00Z");
        Instant lastHealth = Instant.parse("2024-01-03T10:00:00Z");

        Game game = new Game(
                id,
                "engine",
                "http://x",
                "/start",
                "/start",
                "/health",
                lastHealth,
                true,
                "Title",
                "Desc",
                "http://img",
                created,
                updated,
                defaultSettings()
        );

        assertThat(game.healthy()).isEqualTo(true);
        assertThat(game.lastHealthCheck()).isEqualTo(lastHealth);
        assertThat(game.createdAt()).isEqualTo(created);
        assertThat(game.updatedAt()).isEqualTo(updated);
    }

    @Test
    void constructor_nullIdThrows() {
        assertThatThrownBy(() ->
                new Game(
                        null,
                        "engine",
                        "http://x",
                        "/start",
                        "/start",
                        "/health",
                        "Title",
                        "Desc",
                        "http://img",
                        defaultSettings())
        ).isInstanceOf(GameIdNullException.class);
    }

    @Test
    void constructor_blankNameThrows() {
        assertThatThrownBy(() ->
                new Game(
                        gid(),
                        " ",
                        "http://x",
                        "/start",
                        "/start",
                        "/health",
                        "Title",
                        "Desc",
                        "http://img",
                        defaultSettings()
                )
        ).isInstanceOf(GameNameInvalidException.class);
    }

    @Test
    void constructor_blankBaseUrlThrows() {
        assertThatThrownBy(() ->
                new Game(
                        gid(),
                        "engine",
                        " ",
                        "/start",
                        "/start",
                        "/health",
                        "Title",
                        "Desc",
                        "http://img",
                        defaultSettings()
                )
        ).isInstanceOf(GameBaseUrlInvalidException.class);
    }

    @Test
    void constructor_blankStartEndpointThrows() {
        assertThatThrownBy(() ->
                new Game(
                        gid(),
                        "engine",
                        "http://x",
                        "http://x",
                        " ",
                        "/health",
                        "Title",
                        "Desc",
                        "http://img",
                        defaultSettings()
                )
        ).isInstanceOf(GameStartEndpointInvalidException.class);
    }

    @Test
    void constructor_blankHealthEndpointThrows() {
        assertThatThrownBy(() ->
                new Game(
                        gid(),
                        "engine",
                        "http://x",
                        "http://x",
                        "/start",
                        " ",
                        "Title",
                        "Desc",
                        "http://img",
                        defaultSettings()
                )
        ).isInstanceOf(GameHealthEndpointInvalidException.class);
    }

    @Test
    void constructor_invalidMetadataThrows() {
        assertThatThrownBy(() ->
                new Game(
                        gid(),
                        "engine",
                        "http://x",
                        "/start",
                        "/start",
                        "/health",
                        "",
                        "Desc",
                        "http://img",
                        defaultSettings()
                )
        ).isInstanceOf(GameMetadataException.class);
    }

    @Test
    void rename_updatesNameAndTimestamp() {
        Game game = createValidGame();

        Instant before = game.updatedAt();
        game.rename("new-name");

        assertThat(game.name()).isEqualTo("new-name");
        assertThat(game.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void rename_blankThrows() {
        Game game = createValidGame();

        assertThatThrownBy(() -> game.rename(" "))
                .isInstanceOf(GameNameInvalidException.class);
    }

    @Test
    void changeBaseUrl_updatesUrlAndTimestamp() {
        Game game = createValidGame();
        Instant before = game.updatedAt();

        game.changeBaseUrl("http://new");

        assertThat(game.baseUrl()).isEqualTo("http://new");
        assertThat(game.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void changeBaseUrl_blankThrows() {
        Game game = createValidGame();

        assertThatThrownBy(() -> game.changeBaseUrl(" "))
                .isInstanceOf(GameBaseUrlInvalidException.class);
    }

    @Test
    void changeStartEndpoint_updatesAndTimestamp() {
        Game game = createValidGame();
        Instant before = game.updatedAt();

        game.changeStartEndpoint("/play");

        assertThat(game.startEndpoint()).isEqualTo("/play");
        assertThat(game.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void changeStartEndpoint_blankThrows() {
        Game game = createValidGame();

        assertThatThrownBy(() -> game.changeStartEndpoint(" "))
                .isInstanceOf(GameStartEndpointInvalidException.class);
    }

    @Test
    void updateStoreMetadata_updatesFieldsAndTimestamp() {
        Game game = createValidGame();
        Instant before = game.updatedAt();

        game.updateStoreMetadata(
                "New title",
                "New desc",
                "http://newimg");

        assertThat(game.title()).isEqualTo("New title");
        assertThat(game.description()).isEqualTo("New desc");
        assertThat(game.image()).isEqualTo("http://newimg");

        assertThat(game.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void updatedAt_unchangedWithoutMutations() {
        Game game = createValidGame();

        Instant before = game.updatedAt();

        assertThat(game.updatedAt()).isEqualTo(before);
    }
}