package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class LobbyInviteQueryService {
    private final LobbyRepository lobbyRepository;

    public LobbyInviteQueryService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    public List<Lobby> getInvitesForPlayer(final PlayerId playerId, final String token) {
        return lobbyRepository.findAll().stream().filter(lobby -> lobby.isInvited(playerId)).toList();
    }
}