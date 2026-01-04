package be.kdg.team22.communicationservice.domain.chat.channel;

import be.kdg.team22.communicationservice.domain.chat.UserId;

import java.util.Optional;

public interface ChannelRepository {
    Optional<Channel> findById(ChannelId id);

    Optional<Channel> findLobbyChannel(final LobbyId id);

    Optional<Channel> findPrivateChannel(final UserId userId, final UserId otherUserId);

    Optional<Channel> findBotChannel(final UserId userId, final GameId gameId);

    void save(Channel channel);

}