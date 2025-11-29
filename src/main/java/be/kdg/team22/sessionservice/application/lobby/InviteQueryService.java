package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class InviteQueryService {
    private final LobbyRepository repository;

    public InviteQueryService(final LobbyRepository repository) {
        this.repository = repository;
    }

    public List<Lobby> getInvitesForPlayer(final PlayerId playerId) {
        return repository.findAll().stream().filter(lobby -> lobby.isInvited(playerId)).toList();
    }
}