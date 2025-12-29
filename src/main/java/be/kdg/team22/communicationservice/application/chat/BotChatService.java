package be.kdg.team22.communicationservice.application.chat;

import be.kdg.team22.communicationservice.domain.chat.*;
import be.kdg.team22.communicationservice.domain.chat.bot.BotChatRepository;
import be.kdg.team22.communicationservice.domain.chat.bot.BotResponse;
import be.kdg.team22.communicationservice.domain.chat.exceptions.ChatChannelNotFoundException;
import be.kdg.team22.communicationservice.domain.chat.exceptions.NotBotChannelOwnerException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BotChatService {
    private final ChatChannelRepository channelRepository;
    private final BotChatRepository botChatRepository;

    public BotChatService(final ChatChannelRepository channelRepository,
                          final BotChatRepository botChatRepository) {
        this.channelRepository = channelRepository;
        this.botChatRepository = botChatRepository;
    }

    public Channel createChannel(final String userId) {
        return channelRepository
                .findByTypeAndReferenceId(ChatChannelType.BOT, userId)
                .orElseGet(() -> {
                    Channel channel = Channel.createNew(ChatChannelType.BOT, userId);
                    channelRepository.save(channel);
                    return channel;
                });
    }

    public ChatMessage sendMessage(final UUID channelId,
                                   final String senderId,
                                   final String content,
                                   final String gameName) {
        Channel channel = findChannelWithOwnerCheck(channelId, senderId);

        channel.postUserMessage(senderId, content);
        channelRepository.save(channel);

        BotResponse botResponse = botChatRepository.askBot(content, gameName);

        ChatMessage botMessage = channel.postBotMessage("bot", botResponse.answer());
        channelRepository.save(channel);

        return botMessage;
    }

    public List<ChatMessage> getMessages(final UUID channelId,
                                         final String requesterId,
                                         final Instant since) {
        Channel channel = findChannelWithOwnerCheck(channelId, requesterId);
        return since == null ? channel.getMessages() : channel.getMessagesSince(since);
    }

    private Channel findChannelWithOwnerCheck(final UUID channelId, final String userId) {
        Channel channel = channelRepository
                .findById(ChatChannelId.from(channelId))
                .orElseThrow(() -> new ChatChannelNotFoundException(ChatChannelType.BOT, channelId.toString()));

        if (channel.getType() != ChatChannelType.BOT) {
            throw new ChatChannelNotFoundException(ChatChannelType.BOT, channelId.toString());
        }

        if (!channel.getReferenceId().equals(userId)) {
            throw new NotBotChannelOwnerException(userId, channelId.toString());
        }

        return channel;
    }
}

