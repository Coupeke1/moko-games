package be.kdg.team22.gameaclservice.infrastructure.games;

import be.kdg.team22.gameaclservice.events.inbound.ChessRegisterEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RegisterAchievementRequest")
class RegisterAchievementRequestTest {

    @Test
    @DisplayName("should convert chess achievement to register achievement request")
    void testConvertChessAchievementToRegisterAchievementRequest() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "first_move",
                "Make your first move"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("should preserve achievement code as key")
    void testConversionPreservesAchievementCodeAsKey() {
        String code = "first_move";
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                code,
                "Make your first move"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.key()).isEqualTo(code);
    }

    @Test
    @DisplayName("should preserve achievement description")
    void testConversionPreservesDescription() {
        String description = "Make your first move";
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "first_move",
                description
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.description()).isEqualTo(description);
    }

    @Test
    @DisplayName("should convert achievement code to title format")
    void testConversionConvertsCodeToTitle() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "first_move",
                "Description"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.name()).isEqualTo("First Move");
    }

    @Test
    @DisplayName("should handle single word achievement code")
    void testConversionWithSingleWordCode() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "victory",
                "Win a game"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.name()).isEqualTo("Victory");
    }

    @Test
    @DisplayName("should handle multi-word achievement code with underscores")
    void testConversionWithMultiWordCode() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "checkmate_in_five",
                "Achieve checkmate in five moves"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.name()).isEqualTo("Checkmate In Five");
    }

    @Test
    @DisplayName("should capitalize first letter of each word")
    void testConversionCapitalizesFirstLetterOfEachWord() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "stalemate_achieved",
                "Description"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.name()).isEqualTo("Stalemate Achieved");
    }

    @Test
    @DisplayName("should lowercase remaining characters in each word")
    void testConversionLowercasesRemainingCharacters() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "EPIC_VICTORY",
                "Description"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.name()).isEqualTo("Epic Victory");
    }

    @Test
    @DisplayName("should set levels to 2 by default")
    void testConversionSetLevelsToTwo() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "first_move",
                "Description"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.levels()).isEqualTo(2);
    }

    @Test
    @DisplayName("should have all required fields populated")
    void testConversionPopulatesAllFields() {
        String code = "en_passant_capture";
        String description = "Capture a pawn en passant";

        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                code,
                description
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.key()).isEqualTo(code);
        assertThat(result.description()).isEqualTo(description);
        assertThat(result.name()).isNotEmpty();
        assertThat(result.levels()).isEqualTo(2);
    }

    @Test
    @DisplayName("should handle code with many underscores")
    void testConversionWithManyUnderscores() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "win_five_games_in_a_row",
                "Win five games consecutively"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.name()).isEqualTo("Win Five Games In A Row");
        assertThat(result.key()).isEqualTo("win_five_games_in_a_row");
    }

    @Test
    @DisplayName("should handle empty description")
    void testConversionWithEmptyDescription() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "first_move",
                ""
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.description()).isEmpty();
        assertThat(result.key()).isEqualTo("first_move");
        assertThat(result.name()).isEqualTo("First Move");
    }

    @Test
    @DisplayName("should preserve exact description text")
    void testConversionPreservesExactDescriptionText() {
        String description = "This is a very long description with special characters: !@#$%^&*()";
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "special_achievement",
                description
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.description()).isEqualTo(description);
    }

    @Test
    @DisplayName("should handle lowercase achievement codes")
    void testConversionWithLowercaseCode() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "castle_kingside",
                "Castle kingside"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        assertThat(result.name()).isEqualTo("Castle Kingside");
    }

    @Test
    @DisplayName("should trim whitespace from generated title")
    void testConversionTrimsWhitespaceFromTitle() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "promotion_achieved",
                "Achieve pawn promotion"
        );

        RegisterAchievementRequest result = RegisterAchievementRequest.convert(achievement);

        String title = result.name();
        assertThat(title).doesNotStartWith(" ").doesNotEndWith(" ");
    }

    @Test
    @DisplayName("should maintain consistency across multiple conversions")
    void testConversionConsistencyAcrossMultipleConversions() {
        ChessRegisterEvent.ChessAchievement achievement = new ChessRegisterEvent.ChessAchievement(
                "check_mate",
                "Achieve checkmate"
        );

        RegisterAchievementRequest result1 = RegisterAchievementRequest.convert(achievement);
        RegisterAchievementRequest result2 = RegisterAchievementRequest.convert(achievement);

        assertThat(result1.name()).isEqualTo(result2.name());
        assertThat(result1.key()).isEqualTo(result2.key());
        assertThat(result1.description()).isEqualTo(result2.description());
        assertThat(result1.levels()).isEqualTo(result2.levels());
    }
}
