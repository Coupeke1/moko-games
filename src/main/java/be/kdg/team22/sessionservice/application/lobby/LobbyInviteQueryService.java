package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.api.lobby.models.LobbyInviteModel;
import be.kdg.team22.sessionservice.api.lobby.models.PlayerSummaryModel;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyPlayer;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.lobby.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.users.ExternalUserRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.users.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LobbyInviteQueryService {

    private final LobbyRepository lobbyRepository;
    private final ExternalUserRepository userRepository;

    public LobbyInviteQueryService(
            LobbyRepository lobbyRepository,
            ExternalUserRepository userRepository
    ) {
        this.lobbyRepository = lobbyRepository;
        this.userRepository = userRepository;
    }

    public List<LobbyInviteModel> getInvitesForUser(PlayerId userId, String token) {
        return lobbyRepository.findAll().stream()
                .filter(lobby -> lobby.isInvited(userId))
                .map(lobby -> toInviteModel(lobby, token))
                .toList();
    }

    private LobbyInviteModel toInviteModel(Lobby lobby, String token) {
        UserResponse owner = userRepository.getById(lobby.ownerId().value(), token);

        Set<PlayerSummaryModel> players = lobby.players().stream()
                .map(p -> mapPlayer(p, token))
                .collect(Collectors.toSet());

        return new LobbyInviteModel(
                lobby.id().value(),
                lobby.gameId().value(),
                "TODO_GAME_NAME",
                owner.id(),
                owner.username(),
                players,
                lobby.settings().maxPlayers(),
                lobby.status().name(),
                lobby.createdAt()
        );
    }

    private PlayerSummaryModel mapPlayer(LobbyPlayer p, String token) {
        var details = userRepository.getById(p.id(), token);
        return new PlayerSummaryModel(details.id(), details.username());
    }
}

