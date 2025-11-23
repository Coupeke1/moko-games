package be.kdg.team22.sessionservice.application.player;

import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import be.kdg.team22.sessionservice.infrastructure.player.ExternalPlayerRepository;
import be.kdg.team22.sessionservice.infrastructure.player.PlayerResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    private final ExternalPlayerRepository repository;

    public PlayerService(final ExternalPlayerRepository repository) {
        this.repository = repository;
    }

    public Player findPlayer(final PlayerId id, final Jwt token) {
        PlayerResponse response = repository.getById(id.value(), token.getTokenValue()).orElseThrow(id::notFound);
        return new Player(PlayerId.from(response.id()), PlayerName.from(response.username()));
    }
}