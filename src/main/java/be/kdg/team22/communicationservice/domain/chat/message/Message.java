package be.kdg.team22.communicationservice.domain.chat.message;

import be.kdg.team22.communicationservice.domain.chat.SenderId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelId;
import be.kdg.team22.communicationservice.domain.chat.exceptions.MessageEmptyException;
import org.jmolecules.ddd.annotation.Entity;

import java.time.Instant;

@Entity
public record Message(MessageId id,
                      ChannelId channelId,
                      SenderId senderId,
                      String content,
                      Instant timestamp) {

    public Message(final MessageId id, final ChannelId channelId, final SenderId senderId, final String content, final Instant timestamp) {
        this.id = id;
        this.channelId = channelId;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;

        if (content.isBlank()) {
            throw MessageEmptyException.BodyEmpty();
        }
    }

    public Message(final ChannelId channelId, final SenderId senderId, final String content) {
        this(MessageId.create(), channelId, senderId, content, Instant.now());
    }
}