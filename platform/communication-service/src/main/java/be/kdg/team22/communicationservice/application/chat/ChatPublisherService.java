package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelRepository;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import be.kdg.team22.communicationservice.infrastructure.chat.ChatPublisher;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ChatPublisherService {
    private final ChannelRepository repository;
    private final ChatPublisher publisher;

    public ChatPublisherService(final ChannelRepository repository, final ChatPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    public void saveAndPublish(final Channel channel, final Message message) {
        repository.save(channel);
        publisher.publishToPlayers(channel, message);
    }
}
