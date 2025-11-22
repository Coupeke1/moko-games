package be.kdg.team22.sessionservice.application.player;

import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.player.ExternalPlayerRepository;
import be.kdg.team22.sessionservice.infrastructure.player.PlayerResponse;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    private final ExternalPlayerRepository repository;

    public PlayerService(ExternalPlayerRepository repository) {
        this.repository = repository;
    }

    public Player findPlayer(final PlayerId id, final String token) {
        PlayerResponse response = repository.getById(id.value(), token).orElseThrow(id::notFound);
        return new Player(new PlayerId(response.id()), response.username(), response.email());
    }
}