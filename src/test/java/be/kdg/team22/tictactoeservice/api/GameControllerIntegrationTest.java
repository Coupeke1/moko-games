package be.kdg.team22.tictactoeservice.api;

import be.kdg.team22.tictactoeservice.api.models.PlayersModel;
import be.kdg.team22.tictactoeservice.domain.Game;
import be.kdg.team22.tictactoeservice.domain.GameId;
import be.kdg.team22.tictactoeservice.domain.GameStatus;
import be.kdg.team22.tictactoeservice.repository.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private ObjectMapper objectMapper;

    PlayersModel playersModel;

    @BeforeEach
    public void setup() {
        playersModel = new PlayersModel(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test
    void shouldCreateGameWithDefaultSize() throws Exception {
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playersModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.board.length()").value(3))
                .andExpect(jsonPath("$.board").isArray());
    }

    @Test
    void shouldCreateWithCustomSize() throws Exception {


        mockMvc.perform(post("/api/games?size=5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playersModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.board.length()").value(5))
                .andExpect(jsonPath("$.board").isArray());
    }

    @Test
    void shouldRejectInvalidSize() throws Exception {
        mockMvc.perform(post("/api/games?size=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playersModel)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateGameWithCorrectPlayers() throws Exception {
        UUID playerXId = UUID.randomUUID();
        UUID playerOId = UUID.randomUUID();
        playersModel = new PlayersModel(playerXId, playerOId);
        mockMvc.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playersModel)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.playerXId").value(playerXId.toString()))
                .andExpect(jsonPath("$.playerOId").value(playerOId.toString()));
    }

    @Test
    void shouldRejectWithMissingPlayers() throws Exception {
        PlayersModel playerXModel = new PlayersModel(UUID.randomUUID(), null);
        PlayersModel playerOModel = new PlayersModel(null, UUID.randomUUID());
        PlayersModel noPlayersModel = new PlayersModel(null, null);

        mockMvc.perform(post("/api/games?playerXId=%s")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerXModel)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/games?playerOId=%s")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playerOModel)))
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
                        .content(objectMapper.writeValueAsString(playersModel)))
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
                        .content(objectMapper.writeValueAsString(playersModel)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        String id = extractId(createResponse);


        Game game = gameRepository.findById(GameId.fromString(id)).orElseThrow();
        setGameStatus(game, GameStatus.WON);
        gameRepository.save(game);

        mockMvc.perform(post("/api/games/" + id + "/reset"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.board.length()").value(3))
                .andExpect(jsonPath("$.currentPlayer").value("X"))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldreturnbadrequestforunfinshedGame() throws Exception {
        String createResponse = mockMvc.perform(post("/api/games?size=3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(playersModel)))
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
}
