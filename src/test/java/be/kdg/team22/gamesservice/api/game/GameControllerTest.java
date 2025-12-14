package be.kdg.team22.gamesservice.api.game;

import be.kdg.team22.gamesservice.api.game.models.RegisterGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameRequest;
import be.kdg.team22.gamesservice.api.game.models.StartGameResponseModel;
import be.kdg.team22.gamesservice.application.game.GameService;
import be.kdg.team22.gamesservice.domain.game.Game;
import be.kdg.team22.gamesservice.domain.game.GameCategory;
import be.kdg.team22.gamesservice.domain.game.GameId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
@AutoConfigureMockMvc(addFilters = false)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    private Game sampleGame(UUID id) {
        return new Game(
                GameId.from(id),
                "tic-tac-toe",
                "http://engine",
                "/play",
                "/start",
                "/health",
                "Tic Tac Toe",
                "desc",
                "http://img");
    }

    @Test
    @DisplayName("POST /api/games → 202 + body")
    void startGame_returns202AndBody() throws Exception {
        UUID instanceId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        when(gameService.startGame(any(StartGameRequest.class), any()))
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
                  },
                  "aiPlayer": true
                }
                """;

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.gameInstanceId").value(instanceId.toString()));

        ArgumentCaptor<StartGameRequest> captor = ArgumentCaptor.forClass(StartGameRequest.class);
        verify(gameService).startGame(captor.capture(), any());

        StartGameRequest req = captor.getValue();
        assertThat(req.lobbyId().toString()).isEqualTo("00000000-0000-0000-0000-000000000001");
        assertThat(req.gameId().toString()).isEqualTo("00000000-0000-0000-0000-000000000002");
        assertThat(req.players()).hasSize(2);
        assertThat(req.aiPlayer()).isTrue();
    }

    @Test
    @DisplayName("GET /api/games → returns list")
    void getAllGames_returnsList() throws Exception {
        Game g1 = sampleGame(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        Game g2 = sampleGame(UUID.fromString("22222222-2222-2222-2222-222222222222"));

        when(gameService.findAll()).thenReturn(List.of(g1, g2));

        mockMvc.perform(get("/api/games"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.games.length()").value(2))
                .andExpect(jsonPath("$.games[0].id").value(g1.id().value().toString()))
                .andExpect(jsonPath("$.games[1].id").value(g2.id().value().toString()));

        verify(gameService).findAll();
    }

    @Test
    @DisplayName("GET /api/games/{id} → returns game")
    void getGameById_returnsOne() throws Exception {
        UUID id = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        Game game = sampleGame(id);

        when(gameService.findById(GameId.from(id))).thenReturn(game);

        mockMvc.perform(get("/api/games/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Tic Tac Toe"));

        verify(gameService).findById(GameId.from(id));
    }

    @Test
    @DisplayName("GET /api/games/{name} → returns game by name")
    void getGameByName_returnsOne() throws Exception {
        UUID id = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        Game game = sampleGame(id);

        when(gameService.findByName("tic-tac-toe")).thenReturn(game);

        mockMvc.perform(get("/api/games/{name}", "tic-tac-toe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Tic Tac Toe"));

        verify(gameService).findByName("tic-tac-toe");
    }

    @Test
    @DisplayName("PUT /api/games/{id} → updates game")
    void updateGame_returns200AndUpdatedGame() throws Exception {
        UUID id = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        GameId gid = GameId.from(id);
        Game updatedGame = sampleGame(id);

        RegisterGameRequest request = new RegisterGameRequest(
                "tic-tac-toe",
                "http://engine",
                "/play",
                "/start",
                "/health",
                "Tic Tac Toe",
                "desc",
                "http://img",
                BigDecimal.valueOf(29.99),
                GameCategory.STRATEGY
        );

        when(gameService.update(gid, request)).thenReturn(updatedGame);

        String json = """
                {
                  "name": "tic-tac-toe",
                  "backendUrl": "http://engine",
                  "frontendUrl": "/play",
                  "startEndpoint": "/start",
                  "healthEndpoint": "/health",
                  "title": "Tic Tac Toe",
                  "description": "desc",
                  "image": "http://img",
                  "price": 29.99,
                  "category": "STRATEGY"
                }
                """;

        mockMvc.perform(put("/api/games/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Tic Tac Toe"));

        ArgumentCaptor<RegisterGameRequest> captor = ArgumentCaptor.forClass(RegisterGameRequest.class);
        verify(gameService).update(any(GameId.class), captor.capture());

        RegisterGameRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.name()).isEqualTo("tic-tac-toe");
    }

    @Test
    @DisplayName("POST /api/games/register → registers new game")
    void registerGame_returns200AndNewGame() throws Exception {
        UUID id = UUID.fromString("bbbbbbbb-cccc-dddd-eeee-ffffffffffff");
        Game newGame = sampleGame(id);

        when(gameService.register(any(RegisterGameRequest.class))).thenReturn(newGame);

        String json = """
                {
                  "name": "tic-tac-toe",
                  "backendUrl": "http://engine",
                  "frontendUrl": "/play",
                  "startEndpoint": "/start",
                  "healthEndpoint": "/health",
                  "title": "Tic Tac Toe",
                  "description": "desc",
                  "image": "http://img",
                  "price": 19.99,
                  "category": "PARTY"
                }
                """;

        mockMvc.perform(post("/api/games/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tic Tac Toe"))
                .andExpect(jsonPath("$.name").value("tic-tac-toe"));

        ArgumentCaptor<RegisterGameRequest> captor = ArgumentCaptor.forClass(RegisterGameRequest.class);
        verify(gameService).register(captor.capture());

        RegisterGameRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.name()).isEqualTo("tic-tac-toe");
    }
}