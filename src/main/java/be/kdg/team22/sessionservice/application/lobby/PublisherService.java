package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.LobbyPublisher;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PublisherService {
    private final LobbyRepository repository;
    private final LobbyPublisher publisher;

    public PublisherService(final LobbyRepository repository, final LobbyPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public void saveAndPublish(final Lobby lobby) {
        repository.save(lobby);
        publisher.publishToPlayers(lobby);
    }
}
