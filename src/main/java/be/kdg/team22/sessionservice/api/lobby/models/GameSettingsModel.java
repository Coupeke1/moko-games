package be.kdg.team22.sessionservice.api.lobby.models;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TicTacToeSettingsModel.class, name = "ticTacToe"),
        @JsonSubTypes.Type(value = CheckersSettingsModel.class, name = "checkers")
})
public sealed interface GameSettingsModel
        permits TicTacToeSettingsModel, CheckersSettingsModel {
}
