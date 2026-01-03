package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.application.chat.exceptions.InvalidException;
import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.bot.BotResponse;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.InvalidChannelException;
import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.NoAccessException;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import be.kdg.team22.communicationservice.infrastructure.game.ExternalGameRepository;
import be.kdg.team22.communicationservice.infrastructure.game.GameResponse;
import jakarta.transaction.Transactional;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
public class BotChatService {
    private final ChannelService channelService;
    private final ChatPublisherService publisher;
    private final BotChatRepository botChatRepository;
    private final ExternalGameRepository gameRepository;

    public BotChatService(final ChannelService channelService, final ChatPublisherService publisher, final BotChatRepository botChatRepository, final ExternalGameRepository gameRepository) {
        this.channelService = channelService;
        this.publisher = publisher;
        this.botChatRepository = botChatRepository;
        this.gameRepository = gameRepository;
    }

    public List<Message> getMessages(final ChannelId channelId, final Instant since, final Jwt token) {
        Channel channel = channelService.getChannel(channelId);
        checkType(channel);

        UserId userId = UserId.get(token);
        checkForAccess(channel, userId);

        return since == null ? channel.getMessages() : channel.getMessagesSince(since);
    }

    public Message sendMessage(final ChannelId channelId, final String content, final Jwt token) {
        Channel channel = channelService.getChannel(channelId);
        checkType(channel);

        UserId userId = UserId.get(token);
        checkForAccess(channel, userId);

        if (content == null || content.isEmpty())
            throw InvalidException.content();

        Message message = channel.send(userId, content);
        publisher.saveAndPublish(channel, message);


        BotReferenceType referenceType = (BotReferenceType) channel.referenceType();

        GameResponse gameResponse = gameRepository.getGame(referenceType.gameId().value());
        BotResponse botResponse = botChatRepository.ask(content, gameResponse.name());

        Message answer = channel.send(referenceType.botId(), botResponse.answer());
        publisher.saveAndPublish(channel, answer);

        return message;
    }

    private void checkType(final Channel channel) {
        if (channel.referenceType().type().equals(ChannelType.BOT))
            return;

        throw new InvalidChannelException(channel.id(), ChannelType.BOT);
    }

    private void checkForAccess(final Channel channel, final UserId userId) {
        BotReferenceType referenceType = (BotReferenceType) channel.referenceType();
        if (referenceType.userId().equals(userId))
            return;

        throw new NoAccessException(userId, channel.id());
    }
}
