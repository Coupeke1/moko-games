package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelRepository;
import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;
import be.kdg.team22.communicationservice.domain.chat.channel.type.LobbyReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.ReferenceType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ChannelService {
    private final ChannelRepository channelRepository;

    public ChannelService(final ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    public Channel getLobbyChannel(final LobbyId id) {
        return channelRepository.findLobbyChannel(id).orElseGet(() -> {
            ReferenceType type = new LobbyReferenceType(id);
            return new Channel(type);
        });
    }

    public Channel getPrivateChannel(final UserId userId, final UserId otherUserId) {
        return channelRepository.findPrivateChannel(userId, otherUserId).orElseGet(() -> {
            ReferenceType type = new PrivateReferenceType(userId, otherUserId);
            return new Channel(type);
        });
    }
}
