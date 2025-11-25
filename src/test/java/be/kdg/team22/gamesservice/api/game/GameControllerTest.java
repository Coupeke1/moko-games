package be.kdg.team22.gamesservice.api.game;

import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameResponseModel;
import be.kdg.team22.gamesservice.application.game.GameService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @Test
    @DisplayName("POST /api/games – returns 202 with gameInstanceId")
    void startGame_returns202AndBody() throws Exception {
        UUID instanceId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        when(gameService.startGame(any(StartGameRequest.class)))
                .thenReturn(new StartGameResponseModel(instanceId));

        String json = """
                {
                  "lobbyId": "00000000-0000-0000-0000-000000000001",
                  "gameId": "00000000-0000-0000-0000-000000000002",
                  "players": [
                    "11111111-1111-1111-1111-111111111111",
                    "22222222-2222-2222-2222-222222222222"
                  ],
                  "settings": {
                    "type": "checkers",
                    "boardSize": 8,
                    "flyingKings": true
                  }
                }
                """;

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.gameInstanceId").value(instanceId.toString()));

        ArgumentCaptor<StartGameRequest> captor = ArgumentCaptor.forClass(StartGameRequest.class);
        verify(gameService).startGame(captor.capture());

        StartGameRequest captured = captor.getValue();
        assertThat(captured.lobbyId().toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
        assertThat(captured.gameId().toString()).isEqualTo("00000000-0000-0000-0000-000000000002");
        assertThat(captured.players()).hasSize(2);
    }
}