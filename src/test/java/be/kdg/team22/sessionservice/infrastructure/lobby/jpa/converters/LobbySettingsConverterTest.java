package be.kdg.team22.sessionservice.infrastructure.lobby.jpa.converters;

import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.exceptions.SettingsConversionException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LobbySettingsConverterTest {

    private final LobbySettingsConverter converter = new LobbySettingsConverter();

    @Test
    void serializeAndDeserialize_roundTrip_works() {
        LobbySettings original = new LobbySettings(new TicTacToeSettings(3), 4);
        String json = converter.convertToDatabaseColumn(original);
        LobbySettings reconstructed = converter.convertToEntityAttribute(json);

        assertThat(reconstructed.maxPlayers()).isEqualTo(4);
        assertThat(reconstructed.gameSettings()).isInstanceOf(TicTacToeSettings.class);
        assertThat(((TicTacToeSettings) reconstructed.gameSettings()).boardSize()).isEqualTo(3);
    }

    @Test
    void deserialize_invalidJson_throwsSettingsConversionException() {
        String invalidJson = "{ this is not valid json }";

        assertThatThrownBy(() -> converter.convertToEntityAttribute(invalidJson)).isInstanceOf(SettingsConversionException.class).hasMessageContaining("Failed to deserialize");
    }
}
