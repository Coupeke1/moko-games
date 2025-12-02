package be.kdg.team22.checkersservice.api;

import be.kdg.team22.checkersservice.api.models.CreateGameModel;
import be.kdg.team22.checkersservice.domain.player.PlayerRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {
    List<UUID> players;
    CreateGameModel model;

    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        players = List.of(UUID.randomUUID(), UUID.randomUUID());

        model = new CreateGameModel(
                players
        );
    }

    @Test
    void shouldCreateGameWithoutAIPlayer() throws Exception {
        mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.aiPlayer").isEmpty());

    }

    @Test
    void shouldCreateGameWithAIPlayer() throws Exception {
        mock.perform(post("/api/games")
                        .param("aiPlayer", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.aiPlayer").exists())
                .andExpect(jsonPath("$.aiPlayer").value(PlayerRole.WHITE.name()));
    }

    @Test
    void shouldReturnBadRequestWithTooManyPlayers() throws Exception {
        List<UUID> threePlayers = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        CreateGameModel threePlayerModel = new CreateGameModel(threePlayers);

        mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(threePlayerModel))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void shouldReturnBadRequestWithNotEnoughPlayers() throws Exception {
        CreateGameModel onePlayerModel = new CreateGameModel(List.of(UUID.randomUUID()));

        mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onePlayerModel))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").doesNotExist());
    }
}