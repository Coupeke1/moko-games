package be.kdg.team22.tictactoeservice.api;

import be.kdg.team22.tictactoeservice.domain.Game;
import be.kdg.team22.tictactoeservice.domain.GameId;
import be.kdg.team22.tictactoeservice.domain.GameStatus;
import be.kdg.team22.tictactoeservice.repository.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GameRepository gameRepository;

    @Test
    void shouldCreateGameWithDefaultSize() throws Exception {
        mockMvc.perform(post(String.format("/api/games?playerXId=%s&playerOId=%s", UUID.randomUUID(), UUID.randomUUID())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.board.length()").value(3))
                .andExpect(jsonPath("$.board").isArray());
    }

    @Test
    void shouldCreateWithCustomSize() throws Exception {
        mockMvc.perform(post(String.format("/api/games?size=5&playerXId=%s&playerOId=%s", UUID.randomUUID(), UUID.randomUUID())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.board.length()").value(5))
                .andExpect(jsonPath("$.board").isArray());
    }

    @Test
    void shouldRejectInvalidSize() throws Exception {
        mockMvc.perform(post(String.format("/api/games?size=1&playerXId=%s&playerOId=%s",  UUID.randomUUID(), UUID.randomUUID())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFetchExistingGame() throws Exception {
        // Create Game
        String body = mockMvc.perform(post(String.format("/api/games?playerXId=%s&playerOId=%s", UUID.randomUUID(), UUID.randomUUID())))
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
        String createResponse = mockMvc.perform(post(String.format("/api/games?playerXId=%s&playerOId=%s", UUID.randomUUID(), UUID.randomUUID())))
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
        String createResponse = mockMvc.perform(post(String.format("/api/games?size=3&playerXId=%s&playerOId=%s", UUID.randomUUID(), UUID.randomUUID())))
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
