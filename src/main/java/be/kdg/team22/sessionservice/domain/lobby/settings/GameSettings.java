package be.kdg.team22.sessionservice.domain.lobby.settings;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TicTacToeSettings.class, name = "tic_tac_toe"),
        @JsonSubTypes.Type(value = CheckersSettings.class, name = "checkers")
})
public sealed interface GameSettings
        permits TicTacToeSettings, CheckersSettings {}
