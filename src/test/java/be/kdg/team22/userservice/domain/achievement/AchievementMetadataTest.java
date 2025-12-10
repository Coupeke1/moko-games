package be.kdg.team22.userservice.domain.achievement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AchievementMetadataTest {

    @Test
    @DisplayName("getLevels returns correct levels for TICTACTOE_WIN")
    void getLevels_tictactoeWin_returns3() {
        int levels = AchievementMetadata.getLevels("TICTACTOE_WIN");
        assertThat(levels).isEqualTo(3);
    }

    @Test
    @DisplayName("getLevels returns correct levels for TICTACTOE_DRAW")
    void getLevels_tictactoeDraw_returns2() {
        int levels = AchievementMetadata.getLevels("TICTACTOE_DRAW");
        assertThat(levels).isEqualTo(2);
    }

    @Test
    @DisplayName("getLevels returns correct levels for CHECKERS_MULTICAPTURE")
    void getLevels_checkersMulticapture_returns5() {
        int levels = AchievementMetadata.getLevels("CHECKERS_MULTICAPTURE");
        assertThat(levels).isEqualTo(5);
    }

    @Test
    @DisplayName("getLevels returns default 1 for unknown achievement code")
    void getLevels_unknownCode_returnsDefault1() {
        int levels = AchievementMetadata.getLevels("UNKNOWN_ACHIEVEMENT");
        assertThat(levels).isEqualTo(1);
    }

    @Test
    @DisplayName("getLevels returns correct levels for all checkers achievements")
    void getLevels_checkersAchievements() {
        assertThat(AchievementMetadata.getLevels("CHECKERS_WIN")).isEqualTo(3);
        assertThat(AchievementMetadata.getLevels("CHECKERS_DRAW")).isEqualTo(2);
        assertThat(AchievementMetadata.getLevels("CHECKERS_LOSS")).isEqualTo(1);
        assertThat(AchievementMetadata.getLevels("CHECKERS_PROMOTION")).isEqualTo(4);
        assertThat(AchievementMetadata.getLevels("CHECKERS_THREE_KINGS")).isEqualTo(5);
    }
}