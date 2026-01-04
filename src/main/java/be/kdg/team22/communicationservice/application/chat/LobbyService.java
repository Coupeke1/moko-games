package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;
import be.kdg.team22.communicationservice.infrastructure.lobby.ExternalLobbyRepository;
import be.kdg.team22.communicationservice.infrastructure.lobby.models.PlayerModel;
import be.kdg.team22.communicationservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class LobbyService {
    private final ExternalLobbyRepository repository;

    public LobbyService(final ExternalLobbyRepository repository) {
        this.repository = repository;
    }

    public List<PlayerModel> findPlayersInLobby(final LobbyId id, final Jwt token) {
        return repository.findPlayers(id.value(), token);
    }
}