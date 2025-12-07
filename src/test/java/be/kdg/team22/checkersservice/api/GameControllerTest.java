package be.kdg.team22.checkersservice.api;

import be.kdg.team22.checkersservice.api.models.CreateGameModel;
import be.kdg.team22.checkersservice.api.models.GameModel;
import be.kdg.team22.checkersservice.api.models.GameSettingsModel;
import be.kdg.team22.checkersservice.api.models.MoveModel;
import be.kdg.team22.checkersservice.domain.move.KingMovementMode;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {
    List<UUID> players;
    CreateGameModel model;
    UUID gameId;

    @Autowired
    private MockMvc mock;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        players = List.of(UUID.randomUUID(), UUID.randomUUID());
        model = new CreateGameModel(players, new GameSettingsModel(KingMovementMode.FLYING));
        gameId = null;
    }

    @Test
    void createShouldCreateGameWithoutAIPlayer() throws Exception {
        String response = mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.players").isArray())
                .andExpect(jsonPath("$.players.length()").value(2))
                .andExpect(jsonPath("$.aiPlayer").isEmpty())
                .andExpect(jsonPath("$.currentRole").value(PlayerRole.BLACK.name()))
                .andExpect(jsonPath("$.board").isArray())
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andReturn().getResponse().getContentAsString();

        GameModel gameModel = objectMapper.readValue(response, GameModel.class);
        gameId = gameModel.id();
    }

    @Test
    void createShouldCreateGameWithAIPlayer() throws Exception {
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
    void createShouldReturnBadRequestWithTooManyPlayers() throws Exception {
        List<UUID> threePlayers = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        CreateGameModel threePlayerModel = new CreateGameModel(threePlayers, new GameSettingsModel(KingMovementMode.FLYING));

        mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(threePlayerModel))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void createShouldReturnBadRequestWithNotEnoughPlayers() throws Exception {
        CreateGameModel onePlayerModel = new CreateGameModel(List.of(UUID.randomUUID()), new GameSettingsModel(KingMovementMode.FLYING));

        mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(onePlayerModel))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void createShouldReturnBadRequestWithDuplicatePlayers() throws Exception {
        UUID duplicateId = UUID.randomUUID();
        List<UUID> duplicatePlayers = List.of(duplicateId, duplicateId);
        CreateGameModel duplicateModel = new CreateGameModel(duplicatePlayers, new GameSettingsModel(KingMovementMode.FLYING));

        mock.perform(post("/api/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateModel))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetGameById() throws Exception {
        String createResponse = mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameModel createdGame = objectMapper.readValue(createResponse, GameModel.class);
        UUID gameId = createdGame.id();

        mock.perform(get("/api/games/{id}", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()))
                .andExpect(jsonPath("$.players.length()").value(2))
                .andExpect(jsonPath("$.currentRole").value(PlayerRole.BLACK.name()))
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentGame() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mock.perform(get("/api/games/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldMakeValidMove() throws Exception {
        String createResponse = mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameModel createdGame = objectMapper.readValue(createResponse, GameModel.class);
        UUID gameId = createdGame.id();

        UUID blackPlayerId = createdGame.players().stream()
                .filter(p -> p.role() == PlayerRole.BLACK)
                .findFirst()
                .orElseThrow()
                .id();

        int fromCell = 24;
        int toCell = 20;

        MoveModel moveModel = new MoveModel(blackPlayerId, List.of(fromCell, toCell));

        mock.perform(post("/api/games/{id}/move", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moveModel))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()))
                .andExpect(jsonPath("$.currentRole").value(PlayerRole.WHITE.name()))
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }

    @Test
    void shouldRejectMoveWithWrongPlayer() throws Exception {
        String createResponse = mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameModel createdGame = objectMapper.readValue(createResponse, GameModel.class);
        UUID gameId = createdGame.id();

        UUID whitePlayerId = createdGame.players().stream()
                .filter(p -> p.role() == PlayerRole.WHITE)
                .findFirst()
                .orElseThrow()
                .id();

        MoveModel moveModel = new MoveModel(whitePlayerId, List.of(24, 20));

        mock.perform(post("/api/games/{id}/move", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moveModel))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectMoveWithInvalidFromCell() throws Exception {
        String createResponse = mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameModel createdGame = objectMapper.readValue(createResponse, GameModel.class);
        UUID gameId = createdGame.id();

        UUID blackPlayerId = createdGame.players().stream()
                .filter(p -> p.role() == PlayerRole.BLACK)
                .findFirst()
                .orElseThrow()
                .id();

        MoveModel moveModel = new MoveModel(blackPlayerId, List.of(16, 12));

        mock.perform(post("/api/games/{id}/move", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moveModel))
        ).andExpect(status().isNotFound());
    }


    @Test
    void shouldRejectMoveWithInvalidToCell() throws Exception {
        String createResponse = mock.perform(post("/api/games")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameModel createdGame = objectMapper.readValue(createResponse, GameModel.class);
        UUID gameId = createdGame.id();

        UUID blackPlayerId = createdGame.players().stream()
                .filter(p -> p.role() == PlayerRole.BLACK)
                .findFirst()
                .orElseThrow()
                .id();

        MoveModel moveModel = new MoveModel(blackPlayerId, List.of(24, 28));

        mock.perform(post("/api/games/{id}/move", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moveModel))
        ).andExpect(status().isBadRequest());
    }
}