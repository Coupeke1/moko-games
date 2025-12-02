package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.LobbySocketPublisher;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class LobbyPublisherService {
    private final LobbyRepository repository;
    private final LobbySocketPublisher publisher;

    public LobbyPublisherService(final LobbyRepository repository, final LobbySocketPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public void saveAndPublish(final Lobby lobby) {
        repository.save(lobby);
        publisher.publish(lobby);
    }
}
