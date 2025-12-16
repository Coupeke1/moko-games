package be.kdg.team22.gamesservice.api.game;

import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.application.game.GameService;
import be.kdg.team22.gamesservice.domain.game.GameId;
import be.kdg.team22.gamesservice.domain.game.exceptions.GameNotFoundException;
import be.kdg.team22.gamesservice.domain.game.exceptions.PlayersListEmptyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GameController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    private String validJson() {
        return """
                {
                  "lobbyId": "00000000-0000-0000-0000-000000000001",
                  "gameId": "00000000-0000-0000-0000-000000000002",
                  "players": ["11111111-1111-1111-1111-111111111111"],
                  "settings": {
                    "type": "checkers",
                    "boardSize": 8,
                    "flyingKings": true
                  }
                }
                """;
    }

    @Test
    @DisplayName("GameNotFoundException → HTTP 404")
    void gameNotFound_returns404() throws Exception {
        doThrow(new GameNotFoundException(new GameId(UUID.randomUUID())))
                .when(gameService)
                .startGame(any(StartGameRequest.class), isNull());

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PlayersListEmptyException → HTTP 400")
    void emptyPlayers_returns400() throws Exception {
        doThrow(new PlayersListEmptyException())
                .when(gameService)
                .startGame(any(StartGameRequest.class), isNull());

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Malformed JSON → HTTP 400")
    void malformedJson_returns400() throws Exception {

        String malformedJson = """
                {
                  "lobbyId": "00000000-0000-0000-0000-000000000001",
                  "gameId":
                }
                """;

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("IllegalArgumentException → HTTP 400")
    void illegalArgument_returns400() throws Exception {
        doThrow(new IllegalArgumentException("Bad input"))
                .when(gameService)
                .startGame(any(StartGameRequest.class), isNull());

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad input"));
    }
}