package be.kdg.team22.gameaclservice.infrastructure.games;

import be.kdg.team22.gameaclservice.config.ChessInfoProperties;
import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RegisterGameRequest")
class RegisterGameRequestTest {

    @Test
    @DisplayName("should convert chess register event to game request with all required fields")
    void testConvertChessRegisterEventToGameRequest() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://localhost:3000",
                "http://image.url/chess.png",
                List.of(new ChessRegisterEvent.ChessAchievement("first_move", "Make first move")),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("http://backend", BigDecimal.valueOf(9.99), "Board Games");
        String backendUrl = "http://acl-backend";

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, backendUrl);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("should set game name to chess")
    void testConversionSetGameNameToChess() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("http://backend", BigDecimal.ONE, "Games");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.name()).isEqualTo("chess");
    }

    @Test
    @DisplayName("should set backend url from parameter")
    void testConversionSetBackendUrlFromParameter() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, "");
        String backendUrl = "http://acl-backend:8080";

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, backendUrl);

        assertThat(result.backendUrl()).isEqualTo(backendUrl);
    }

    @Test
    @DisplayName("should preserve frontend url from event")
    void testConversionPreservesFrontendUrl() {
        String frontendUrl = "http://chess-frontend.example.com";
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                frontendUrl,
                "http://image.url",
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, "");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.frontendUrl()).isEqualTo(frontendUrl);
    }

    @Test
    @DisplayName("should set start endpoint to /api/games/chess")
    void testConversionSetStartEndpoint() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, "");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.startEndpoint()).isEqualTo("/api/games/chess");
    }

    @Test
    @DisplayName("should set health endpoint to /actuator/health")
    void testConversionSetHealthEndpoint() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, "");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.healthEndpoint()).isEqualTo("/actuator/health");
    }

    @Test
    @DisplayName("should set title to Chess")
    void testConversionSetTitle() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, "");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.title()).isEqualTo("Chess");
    }

    @Test
    @DisplayName("should set description for chess game")
    void testConversionSetDescription() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, "");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.description()).isEqualTo("Play chess against other players online.");
    }

    @Test
    @DisplayName("should preserve image url from event")
    void testConversionPreservesImageUrl() {
        String imageUrl = "http://cdn.example.com/chess-board.png";
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                imageUrl,
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, "");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.image()).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("should set price from chess info properties")
    void testConversionSetPriceFromProperties() {
        BigDecimal price = BigDecimal.valueOf(19.99);
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", price, "");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.price()).isEqualByComparingTo(price);
    }

    @Test
    @DisplayName("should set category from chess info properties")
    void testConversionSetCategoryFromProperties() {
        String category = "Strategy Games";
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, category);

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.category()).isEqualTo(category);
    }

    @Test
    @DisplayName("should convert achievements from event")
    void testConversionConvertsAchievements() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                List.of(
                        new ChessRegisterEvent.ChessAchievement("first_move", "Make first move"),
                        new ChessRegisterEvent.ChessAchievement("checkmate", "Achieve checkmate")
                ),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, "");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.achievements()).hasSize(2);
    }

    @Test
    @DisplayName("should handle empty achievements list")
    void testConversionWithNoAchievements() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                List.of(),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, "");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.achievements()).isEmpty();
    }

    @Test
    @DisplayName("should handle multiple achievements")
    void testConversionWithMultipleAchievements() {
        List<ChessRegisterEvent.ChessAchievement> achievements = List.of(
                new ChessRegisterEvent.ChessAchievement("first_move", "Make first move"),
                new ChessRegisterEvent.ChessAchievement("checkmate", "Achieve checkmate"),
                new ChessRegisterEvent.ChessAchievement("stalemate", "Achieve stalemate"),
                new ChessRegisterEvent.ChessAchievement("check", "Give check")
        );

        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend",
                "http://image.url",
                achievements,
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("", BigDecimal.ONE, "");

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, "http://acl");

        assertThat(result.achievements()).hasSize(4);
    }

    @Test
    @DisplayName("should create complete request with all fields populated")
    void testConversionCreatesCompleteRequest() {
        ChessRegisterEvent event = new ChessRegisterEvent(
                UUID.randomUUID(),
                "http://frontend:3000",
                "http://cdn.com/chess.png",
                List.of(new ChessRegisterEvent.ChessAchievement("victory", "Win a game")),
                "GAME_REGISTERED",
                LocalDateTime.now()
        );

        ChessInfoProperties properties = createChessInfoProperties("http://chess-backend", BigDecimal.valueOf(9.99), "Board Games");
        String aclBackendUrl = "http://acl-backend:8080";

        RegisterGameRequest result = RegisterGameRequest.convert(event, properties, aclBackendUrl);

        assertThat(result.name()).isEqualTo("chess");
        assertThat(result.backendUrl()).isEqualTo(aclBackendUrl);
        assertThat(result.frontendUrl()).isEqualTo("http://frontend:3000");
        assertThat(result.title()).isEqualTo("Chess");
        assertThat(result.image()).isEqualTo("http://cdn.com/chess.png");
        assertThat(result.category()).isEqualTo("Board Games");
        assertThat(result.achievements()).isNotEmpty();
    }

    private ChessInfoProperties createChessInfoProperties(String backendUrl, BigDecimal price, String category) {
        ChessInfoProperties properties = new ChessInfoProperties();
        properties.setBackendUrl(backendUrl);
        properties.setPrice(price);
        properties.setCategory(category);
        return properties;
    }
}
