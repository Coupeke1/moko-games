package be.kdg.team22.tictactoeservice.api;

import be.kdg.team22.tictactoeservice.api.models.CreateGameModel;
import be.kdg.team22.tictactoeservice.api.models.GameSettingsModel;
import be.kdg.team22.tictactoeservice.api.models.MoveModel;
import be.kdg.team22.tictactoeservice.domain.game.Game;
import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.game.GameStatus;
import be.kdg.team22.tictactoeservice.infrastructure.ai.AiMoveResponse;
import be.kdg.team22.tictactoeservice.infrastructure.ai.ExternalAiRepository;
import be.kdg.team22.tictactoeservice.infrastructure.game.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerIntegrationTest {
    List<UUID> players;
    CreateGameModel model;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GameRepository repository;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExternalAiRepository aiRepository;

    @BeforeEach
    public void setup() {
        players = List.of(UUID.randomUUID(), UUID.randomUUID());

        model = new CreateGameModel(
                players,
                new GameSettingsModel(3)
        );
    }

    @Test
    void shouldCreateGameWithDefaultSize() throws Exception {
        CreateGameModel defaultModel = new CreateGameModel(
                players,
                new GameSettingsModel(0)
        );

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.board.length()").value(3))
                .andExpect(jsonPath("$.board").isArray());
    }

    @Test
    void shouldCreateWithCustomSize() throws Exception {
        CreateGameModel modelSizeFive = new CreateGameModel(
                players,
                new GameSettingsModel(5)
        );

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modelSizeFive)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.board.length()").value(5))
                .andExpect(jsonPath("$.board").isArray());
    }

    @Test
    void shouldRejectInvalidSize() throws Exception {
        CreateGameModel modelSizeOne = new CreateGameModel(
                List.of(players.getFirst()),
                new GameSettingsModel(1)
        );

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modelSizeOne)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateGameWithCorrectPlayers() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.players[?(@.role == 'X')].id").value(players.getFirst().toString()))
                .andExpect(jsonPath("$.players[?(@.role == 'O')].id").value(players.get(1).toString()));
    }

    @Test
    void shouldRejectWithMissingPlayers() throws Exception {
        CreateGameModel onePlayerModel = new CreateGameModel(
                List.of(players.getFirst()),
                new GameSettingsModel(3)
        );

        CreateGameModel noPlayersModel = new CreateGameModel(
                List.of(),
                new GameSettingsModel(3)
        );

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onePlayerModel)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(noPlayersModel)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/games"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFetchExistingGame() throws Exception {
        // Create Game
        String body = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model)))
                .andReturn().getResponse().getContentAsString();

        String id = extractId(body);

        // Fetch
        mockMvc.perform(get("/api/games/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void shouldReturn404ForUnknownGame() throws Exception {
        mockMvc.perform(get("/api/games/00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldResetFinishedGame() throws Exception {
        String createResponse = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String id = extractId(createResponse);


        Game game = repository.findById(GameId.fromString(id)).orElseThrow();
        setGameStatus(game, GameStatus.WON);
        repository.save(game);

        mockMvc.perform(post("/api/games/" + id + "/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.board.length()").value(3))
                .andExpect(jsonPath("$.currentRole").value("X"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldReturnBadRequestForUnfinishedGame() throws Exception {
        String createResponse = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String id = extractId(createResponse);

        mockMvc.perform(post("/api/games/" + id + "/reset"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenResetUnknownGame() throws Exception {
        mockMvc.perform(post("/api/games/00000000-0000-0000-0000-000000000000/reset"))
                .andExpect(status().isNotFound());
    }

    private String extractId(String json) {
        return json.split("\"id\":\"")[1].split("\"")[0];
    }

    private void setGameStatus(Game game, GameStatus status) throws NoSuchFieldException, IllegalAccessException {
        Field statusField = Game.class.getDeclaredField("status");
        statusField.setAccessible(true);
        statusField.set(game, status);
    }

    @Test
    void shouldMakeValidMove() throws Exception {
        String createResponse = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String gameId = extractId(createResponse);
        Game game = repository.findById(GameId.fromString(gameId)).orElseThrow();

        MoveModel moveModel = new MoveModel(game.id().value(), game.currentPlayer().id().value(), 0, 1);

        mockMvc.perform(post("/api/games/" + gameId + "/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moveModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId))
                .andExpect(jsonPath("$.board[0][1]").value(game.players().first().role().toString()))
                .andExpect(jsonPath("$.currentRole").value(game.players().stream().toList().get(1).role().toString()))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void shouldReturnBadRequestForInvalidMove() throws Exception {
        String createResponse = mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String gameId = extractId(createResponse);
        Game game = repository.findById(GameId.fromString(gameId)).orElseThrow();

        MoveModel moveModel = new MoveModel(game.id().value(), game.currentPlayer().id().value(), -1, 1);

        mockMvc.perform(post("/api/games/" + gameId + "/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moveModel)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void shouldPerformAiTurnAfterHumanPlayerTurn() throws Exception {
        when(aiRepository.requestMove(any()))
                .thenReturn(new AiMoveResponse(
                        "TICTACTOE",
                        "IN_PROGRESS",
                        0,
                        1
                ));

        CreateGameModel onePlayerModel = new CreateGameModel(
                List.of(UUID.randomUUID(),UUID.randomUUID()),
                new GameSettingsModel(3)
        );

        String createResponse = mockMvc.perform(post("/api/games")
                        .param("aiPlayer", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onePlayerModel)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String gameId = extractId(createResponse);

        Game game = repository.findById(GameId.fromString(gameId)).orElseThrow();
        MoveModel humanMove = new MoveModel(game.id().value(), game.currentPlayer().id().value(), 0, 0);

        mockMvc.perform(post("/api/games/" + gameId + "/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(humanMove)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.board[0][0]").value("X"))
                .andExpect(jsonPath("$.currentRole").value("O"));

        Thread.sleep(300);

        mockMvc.perform(get("/api/games/" + gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.board[0][1]").value("O"))
                .andExpect(jsonPath("$.currentRole").value("X"));
    }
}