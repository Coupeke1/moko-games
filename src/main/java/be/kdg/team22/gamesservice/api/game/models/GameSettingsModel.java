package be.kdg.team22.gamesservice.api.game.models;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CheckersSettingsModel.class,   name = "checkers"),
        @JsonSubTypes.Type(value = TicTacToeSettingsModel.class, name = "tic-tac-toe")
})
public sealed interface GameSettingsModel
        permits CheckersSettingsModel, TicTacToeSettingsModel {
}
