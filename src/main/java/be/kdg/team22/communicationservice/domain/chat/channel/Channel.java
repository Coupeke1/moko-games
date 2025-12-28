package be.kdg.team22.communicationservice.domain.chat.channel;

import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.exceptions.NoAccessException;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.ReferenceType;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import org.jmolecules.ddd.annotation.AggregateRoot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AggregateRoot
public class Channel {
    private final ChannelId id;
    private final ReferenceType referenceType;
    private final List<Message> messages = new ArrayList<>();

    public Channel(final ChannelId id, final ReferenceType referenceType) {
        this.id = id;
        this.referenceType = referenceType;
    }

    public Channel(final ChannelId id, final ReferenceType referenceType, final List<Message> messages) {
        this.id = id;
        this.referenceType = referenceType;
        this.messages.addAll(messages);
    }

    public Channel(final ReferenceType referenceType) {
        id = ChannelId.create();
        this.referenceType = referenceType;
    }

    public ChannelId id() {
        return id;
    }

    public ReferenceType referenceType() {
        return referenceType;
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public List<Message> getMessagesSince(final Instant since) {
        return messages.stream().filter(m -> m.timestamp().isAfter(since)).toList();
    }

    public Message send(final UserId id, final String content) {
        Message message = new Message(this.id, id, content);
        messages.add(message);
        return message;
    }
}