package be.kdg.team22.communicationservice.infrastructure.chat.jpa;

import be.kdg.team22.communicationservice.domain.chat.ChatChannel;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelId;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_channels")
public class ChatChannelEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatChannelType type;

    @Column(nullable = false)
    private String referenceId;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessageEntity> messages = new ArrayList<>();

    protected ChatChannelEntity() {
    }

    public ChatChannelEntity(UUID id, ChatChannelType type, String referenceId) {
        this.id = id;
        this.type = type;
        this.referenceId = referenceId;
    }

    public static ChatChannelEntity from(ChatChannel channel) {
        ChatChannelEntity entity = new ChatChannelEntity(
                channel.getId().value(),
                channel.getType(),
                channel.getReferenceId()
        );

        for (ChatMessage m : channel.getMessages()) {
            entity.messages.add(ChatMessageEntity.from(m, entity));
        }

        return entity;
    }

    public ChatChannel to() {

        return new ChatChannel(
                ChatChannelId.from(this.id),
                this.type,
                this.referenceId,
                messages.stream().map(ChatMessageEntity::to).toList());
    }

    public UUID getId() {
        return id;
    }

    public ChatChannelType getType() {
        return type;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public List<ChatMessageEntity> getMessages() {
        return messages;
    }
}