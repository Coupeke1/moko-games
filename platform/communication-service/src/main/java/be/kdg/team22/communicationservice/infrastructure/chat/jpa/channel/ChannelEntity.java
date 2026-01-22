package be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel;

import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelId;
import be.kdg.team22.communicationservice.domain.chat.channel.type.ReferenceType;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.type.ReferenceTypeEntity;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.message.MessageEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_channels")
public class ChannelEntity {
    @Id
    private UUID id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "reference_type_id")
    private ReferenceTypeEntity referenceType;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<MessageEntity> messages = new ArrayList<>();

    protected ChannelEntity() {
    }

    public ChannelEntity(final UUID id, final ReferenceTypeEntity referenceType) {
        this.id = id;
        this.referenceType = referenceType;
    }

    public static ChannelEntity from(final Channel channel) {
        ReferenceTypeEntity referenceType = ReferenceTypeEntity.from(channel.referenceType());
        ChannelEntity entity = new ChannelEntity(channel.id().value(), referenceType);

        for (Message message : channel.getMessages()) {
            entity.messages.add(MessageEntity.from(message, entity));
        }

        return entity;
    }

    public Channel to() {
        ReferenceType referenceType = this.referenceType.to();
        List<Message> messages = this.messages.stream().map(MessageEntity::to).toList();
        return new Channel(ChannelId.from(this.id), referenceType, messages);
    }

    public UUID id() {
        return id;
    }
}