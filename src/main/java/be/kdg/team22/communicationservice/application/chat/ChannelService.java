package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.bot.BotId;
import be.kdg.team22.communicationservice.domain.chat.channel.*;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.LobbyReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.ReferenceType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ChannelService {
    private final ChannelRepository repository;

    public ChannelService(final ChannelRepository repository) {
        this.repository = repository;
    }

    public Channel getChannel(final ChannelId id) {
        return repository.findById(id).orElseThrow(id::notFound);
    }

    public Channel getOrCreateLobbyChannel(final LobbyId id) {
        return repository.findLobbyChannel(id).orElseGet(() -> {
            ReferenceType type = new LobbyReferenceType(id);
            Channel channel = new Channel(type);
            repository.save(channel);
            return channel;
        });
    }

    public Channel getOrCreateBotChannel(final UserId userId, final GameId gameId, final BotId botId) {
        return repository.findBotChannel(userId, gameId).orElseGet(() -> {
            ReferenceType type = new BotReferenceType(userId, botId, gameId);
            Channel channel = new Channel(type);
            repository.save(channel);
            return channel;
        });
    }

    public Channel getOrCreatePrivateChannel(final UserId userId, final UserId otherUserId) {
        return repository.findPrivateChannel(userId, otherUserId).orElseGet(() -> {
            ReferenceType type = new PrivateReferenceType(userId, otherUserId);
            Channel channel = new Channel(type);
            repository.save(channel);
            return channel;
        });
    }
}
