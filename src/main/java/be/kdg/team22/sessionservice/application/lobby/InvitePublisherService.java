package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.lobby.InviteSocketPublisher;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class InvitePublisherService {
    private final InviteSocketPublisher publisher;

    public InvitePublisherService(final InviteSocketPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(final PlayerId id, final Lobby lobby) {
        publisher.publish(id, lobby);
    }
}
