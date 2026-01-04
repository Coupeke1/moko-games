package be.kdg.team22.communicationservice.infrastructure.chat;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.*;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.ChannelEntity;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.JpaChannelRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DbChannelRepository implements ChannelRepository {
    private final JpaChannelRepository channelRepository;

    public DbChannelRepository(final JpaChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Optional<Channel> findById(final ChannelId id) {
        return channelRepository.findById(id.value()).map(ChannelEntity::to);
    }

    @Override
    public Optional<Channel> findLobbyChannel(final LobbyId id) {
        return channelRepository.findLobbyChannel(id.value()).map(ChannelEntity::to);
    }

    @Override
    public Optional<Channel> findPrivateChannel(final UserId userId, final UserId otherUserId) {
        return channelRepository.findPrivateChannel(userId.value(), otherUserId.value()).map(ChannelEntity::to);
    }

    @Override
    public Optional<Channel> findBotChannel(final UserId userId, final GameId gameId) {
        return channelRepository.findBotChannel(userId.value(), gameId.value()).map(ChannelEntity::to);
    }

    @Override
    public void save(final Channel channel) {
        channelRepository.save(ChannelEntity.from(channel));
    }
}