package be.kdg.team22.gamesservice.domain.game;

import be.kdg.team22.gamesservice.domain.game.exceptions.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
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
                "/start",
                "Title",
                "Description",
                BigDecimal.TEN,
                "http://img",
                "http://store"
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
                "Title",
                "Desc",
                BigDecimal.ONE,
                "http://img",
                "http://store"
        );

        assertThat(game.id()).isEqualTo(id);
        assertThat(game.name()).isEqualTo("engine");
        assertThat(game.baseUrl()).isEqualTo("http://x");
        assertThat(game.startEndpoint()).isEqualTo("/start");
        assertThat(game.title()).isEqualTo("Title");
        assertThat(game.description()).isEqualTo("Desc");
        assertThat(game.price()).isEqualTo(BigDecimal.ONE);
        assertThat(game.imageUrl()).isEqualTo("http://img");
        assertThat(game.storeUrl()).isEqualTo("http://store");

        assertThat(game.createdAt()).isNotNull();
        assertThat(game.updatedAt()).isEqualTo(game.createdAt());
    }

    @Test
    void fullConstructor_restoresState() {
        GameId id = gid();
        Instant created = Instant.parse("2024-01-01T10:00:00Z");
        Instant updated = Instant.parse("2024-01-02T10:00:00Z");

        Game game = new Game(
                id,
                "engine",
                "http://x",
                "/start",
                "Title",
                "Desc",
                BigDecimal.TEN,
                "http://img",
                "http://store",
                created,
                updated
        );

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
                        "Title",
                        "Desc",
                        BigDecimal.TEN,
                        "http://img",
                        "http://store"
                )
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
                        "Title",
                        "Desc",
                        BigDecimal.TEN,
                        "http://img",
                        "http://store"
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
                        "Title",
                        "Desc",
                        BigDecimal.TEN,
                        "http://img",
                        "http://store"
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
                        " ",
                        "Title",
                        "Desc",
                        BigDecimal.TEN,
                        "http://img",
                        "http://store"
                )
        ).isInstanceOf(GameStartEndpointInvalidException.class);
    }

    @Test
    void constructor_invalidMetadataThrows() {
        assertThatThrownBy(() ->
                new Game(
                        gid(),
                        "engine",
                        "http://x",
                        "/start",
                        "",
                        "Desc",
                        BigDecimal.TEN,
                        "http://img",
                        "http://store"
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
                new BigDecimal("20"),
                "http://newimg",
                "http://storeurl"
        );

        assertThat(game.title()).isEqualTo("New title");
        assertThat(game.description()).isEqualTo("New desc");
        assertThat(game.price()).isEqualTo(new BigDecimal("20"));
        assertThat(game.imageUrl()).isEqualTo("http://newimg");
        assertThat(game.storeUrl()).isEqualTo("http://storeurl");

        assertThat(game.updatedAt()).isAfterOrEqualTo(before);
    }

    @Test
    void updateStoreMetadata_invalidPriceThrows() {
        Game game = createValidGame();

        assertThatThrownBy(() ->
                game.updateStoreMetadata(
                        "Title",
                        "Desc",
                        new BigDecimal("-1"),
                        "http://img",
                        "http://store"
                )
        ).isInstanceOf(GameMetadataException.class);
    }

    @Test
    void updatedAt_unchangedWithoutMutations() {
        Game game = createValidGame();

        Instant before = game.updatedAt();

        assertThat(game.updatedAt()).isEqualTo(before);
    }
}