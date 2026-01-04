package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.channel.GameId;
import be.kdg.team22.communicationservice.infrastructure.game.ExternalGameRepository;
import be.kdg.team22.communicationservice.infrastructure.game.GameResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GameService {
    private final ExternalGameRepository repository;

    public GameService(final ExternalGameRepository repository) {
        this.repository = repository;
    }

    public GameResponse getGame(final GameId id) {
        return repository.getGame(id.value());
    }
}