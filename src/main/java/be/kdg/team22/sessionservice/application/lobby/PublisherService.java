package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.lobby.LobbyPublisher;
import be.kdg.team22.sessionservice.infrastructure.lobby.RemovalReason;
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

    public void savePublishAndNotifyRemoved(final Lobby lobby, final PlayerId removedPlayerId, final RemovalReason reason) {
        publisher.publishToPlayerWithReason(removedPlayerId, reason);
        repository.save(lobby);
        publisher.publishToPlayers(lobby);
    }
}