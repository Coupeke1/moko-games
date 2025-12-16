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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
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
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        players = List.of(UUID.randomUUID(), UUID.randomUUID());
        model = new CreateGameModel(players, new GameSettingsModel(KingMovementMode.FLYING));
        gameId = null;
    }

    @Test
    void createShouldCreateGameWithoutBotPlayer() throws Exception {
        UsernamePasswordAuthenticationToken auth = authWithUser(model.players().getFirst());
        String response = mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.players").isArray())
                .andExpect(jsonPath("$.players.length()").value(2))
                .andExpect(jsonPath("$.botPlayer").isEmpty())
                .andExpect(jsonPath("$.currentRole").value(PlayerRole.BLACK.name()))
                .andExpect(jsonPath("$.board").isArray())
                .andExpect(jsonPath("$.status").value("RUNNING"))
                .andReturn().getResponse().getContentAsString();

        GameModel gameModel = objectMapper.readValue(response, GameModel.class);
        gameId = gameModel.id();
    }

    @Test
    void createShouldCreateGameWithBotPlayer() throws Exception {
        UsernamePasswordAuthenticationToken auth = authWithUser(model.players().getFirst());
        mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
                        .param("aiPlayer", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.botPlayer").exists())
                .andExpect(jsonPath("$.botPlayer").value(PlayerRole.WHITE.name()));
    }

    @Test
    void createShouldReturnBadRequestWithTooManyPlayers() throws Exception {
        List<UUID> threePlayers = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());
        CreateGameModel threePlayerModel = new CreateGameModel(threePlayers, new GameSettingsModel(KingMovementMode.FLYING));
        UsernamePasswordAuthenticationToken auth = authWithUser(threePlayerModel.players().getFirst());

        mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(threePlayerModel))
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void createShouldReturnBadRequestWithNotEnoughPlayers() throws Exception {
        CreateGameModel onePlayerModel = new CreateGameModel(List.of(UUID.randomUUID()), new GameSettingsModel(KingMovementMode.FLYING));
        UsernamePasswordAuthenticationToken auth = authWithUser(onePlayerModel.players().getFirst());

        mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
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
        UsernamePasswordAuthenticationToken auth = authWithUser(duplicateModel.players().getFirst());

        mockMvc.perform(post("/api/games")
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateModel))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetGameById() throws Exception {
        UsernamePasswordAuthenticationToken auth = authWithUser(model.players().getFirst());

        String createResponse = mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(model))
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GameModel createdGame = objectMapper.readValue(createResponse, GameModel.class);
        UUID gameId = createdGame.id();

        mockMvc.perform(get("/api/games/{id}", gameId)
                        .with(authentication(auth))
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
        UsernamePasswordAuthenticationToken auth = authWithUser(nonExistentId);

        mockMvc.perform(get("/api/games/{id}", nonExistentId)
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldMakeValidMove() throws Exception {
        UsernamePasswordAuthenticationToken auth = authWithUser(model.players().getFirst());

        String createResponse = mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
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

        mockMvc.perform(post("/api/games/{id}/move", gameId)
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moveModel))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(gameId.toString()))
                .andExpect(jsonPath("$.currentRole").value(PlayerRole.WHITE.name()))
                .andExpect(jsonPath("$.status").value("RUNNING"));
    }

    @Test
    void shouldRejectMoveWithWrongPlayer() throws Exception {
        UsernamePasswordAuthenticationToken auth = authWithUser(model.players().getFirst());

        String createResponse = mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
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

        UsernamePasswordAuthenticationToken whiteAuth = authWithUser(whitePlayerId);
        MoveModel moveModel = new MoveModel(whitePlayerId, List.of(24, 20));

        mockMvc.perform(post("/api/games/{id}/move", gameId)
                .with(authentication(whiteAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moveModel))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectMoveWithInvalidFromCell() throws Exception {
        UsernamePasswordAuthenticationToken auth = authWithUser(model.players().getFirst());

        String createResponse = mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
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

        mockMvc.perform(post("/api/games/{id}/move", gameId)
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moveModel))
        ).andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectMoveWithInvalidToCell() throws Exception {
        UsernamePasswordAuthenticationToken auth = authWithUser(model.players().getFirst());

        String createResponse = mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
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

        mockMvc.perform(post("/api/games/{id}/move", gameId)
                .with(authentication(auth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moveModel))
        ).andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectMoveWithoutJwtToken() throws Exception {
        UsernamePasswordAuthenticationToken auth = authWithUser(model.players().getFirst());

        String createResponse = mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
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

        mockMvc.perform(post("/api/games/{id}/move", gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moveModel))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectMoveWithMismatchingJwtToken() throws Exception {
        UsernamePasswordAuthenticationToken auth = authWithUser(model.players().getFirst());

        String createResponse = mockMvc.perform(post("/api/games")
                        .with(authentication(auth))
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

        UsernamePasswordAuthenticationToken wrongAuth = authWithUser(UUID.randomUUID());
        MoveModel moveModel = new MoveModel(blackPlayerId, List.of(24, 28));

        mockMvc.perform(post("/api/games/{id}/move", gameId)
                .with(authentication(wrongAuth))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(moveModel))
        ).andExpect(status().isForbidden());
    }

    private UsernamePasswordAuthenticationToken authWithUser(UUID id) {
        Jwt jwt = Jwt.withTokenValue("token-" + id)
                .header("alg", "none")
                .subject(id.toString())
                .claim("preferred_username", "mathias")
                .claim("email", "m@a")
                .build();

        return new UsernamePasswordAuthenticationToken(jwt, jwt.getTokenValue(), List.of());
    }
}