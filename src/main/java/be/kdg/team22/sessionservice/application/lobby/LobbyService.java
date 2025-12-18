package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.CreateLobbyModel;
import be.kdg.team22.sessionservice.api.lobby.models.UpdateLobbySettingsModel;
import be.kdg.team22.sessionservice.application.player.PlayerService;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.PlayersException;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.chat.ExternalChatRepository;
import be.kdg.team22.sessionservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.sessionservice.infrastructure.games.StartGameRequest;
import be.kdg.team22.sessionservice.infrastructure.games.StartGameResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class LobbyService {
    private final LobbyRepository repository;
    private final PublisherService publisher;
    private final PlayerService playerService;
    private final ExternalGamesRepository gamesRepository;
    private final ExternalChatRepository chatRepository;

    public LobbyService(final LobbyRepository repository, final PublisherService publisher, final PlayerService playerService, final ExternalGamesRepository gamesRepository, final ExternalChatRepository chatRepository) {
        this.repository = repository;
        this.publisher = publisher;
        this.playerService = playerService;
        this.gamesRepository = gamesRepository;
        this.chatRepository = chatRepository;
    }

    public Lobby createLobby(final GameId gameId, final PlayerId ownerId, final CreateLobbyModel model, final Jwt token) {
        Player owner = playerService.findPlayer(ownerId, token);

        Map<String, Object> input = (model.settings() == null) ? Map.of() : model.settings();
        Map<String, Object> resolved = gamesRepository.validateAndResolveSettings(gameId, input, token);

        int maxPlayers = (model.maxPlayers() == null) ? 2 : model.maxPlayers();

        Lobby lobby = new Lobby(gameId, owner, new LobbySettings(maxPlayers, resolved));

        publisher.saveAndPublish(lobby);
        chatRepository.createLobbyChat(lobby.id(), token);
        return lobby;
    }

    public Lobby findLobby(final LobbyId id) {
        return repository.findById(id).orElseThrow(id::notFound);
    }

    public List<Lobby> findAllLobbies() {
        return repository.findAll();
    }

    public Lobby closeLobby(final LobbyId lobbyId, final PlayerId ownerId) {
        Lobby lobby = findLobby(lobbyId);
        lobby.close(ownerId);

        publisher.saveAndPublish(lobby);
        return lobby;
    }

    public Lobby updateSettings(final LobbyId lobbyId, final PlayerId ownerId, final UpdateLobbySettingsModel model, final Jwt token) {
        Lobby lobby = findLobby(lobbyId);

        int newMaxPlayers = model.maxPlayers() != null ? model.maxPlayers() : lobby.settings().maxPlayers();

        Map<String, Object> merged = new HashMap<>(lobby.settings().gameSettings());
        if (model.settings() != null)
            merged.putAll(model.settings());

        Map<String, Object> resolved = gamesRepository.validateAndResolveSettings(lobby.gameId(), merged, token);

        lobby.changeSettings(ownerId, new LobbySettings(newMaxPlayers, resolved));
        publisher.saveAndPublish(lobby);
        return lobby;
    }

    public Lobby startLobby(final LobbyId lobbyId, final PlayerId ownerId, final Jwt token) {
        Lobby lobby = findLobby(lobbyId);

        lobby.ensureOwner(ownerId);
        lobby.ensureAllPlayersReady();

        if (lobby.size() <= 1)
            throw PlayersException.tooLittle();

        if (lobby.size() > lobby.settings().maxPlayers())
            throw PlayersException.tooMany();

        List<UUID> players = new ArrayList<>(lobby.players().stream().map(p -> p.id().value()).toList());

        if (lobby.hasBot()) {
            if (lobby.players().size() > 1)
                throw PlayersException.tooMany();

            players.add(lobby.bot().id().value());
        }

        StartGameResponse response = gamesRepository.startGame(new StartGameRequest(lobbyId.value(), lobby.gameId().value(), players, lobby.settings().gameSettings(), lobby.hasBot()), token);

        lobby.markStarted(GameId.from(response.gameInstanceId()));

        publisher.saveAndPublish(lobby);
        return lobby;
    }

    public List<Lobby> getInvitesFromPlayer(final PlayerId id, final GameId game) {
        return repository.findInvitesFromPlayerId(id, game);
    }

    public Optional<Lobby> findByStartedGameId(final GameId gameId) {
        return repository.findByStartedGameId(gameId);
    }

    public Lobby findByPlayer(final PlayerId id) {
        return repository.findByPlayerId(id).orElseThrow(() -> new LobbyNotFoundException(id));
    }
}