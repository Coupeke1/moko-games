package be.kdg.team22.communicationservice.api.chat.models;

import be.kdg.team22.communicationservice.domain.chat.ChatChannel;

import java.util.List;
import java.util.UUID;

public record ChatHistoryResponse(
        UUID channelId,
        String type,
        String referenceId,
        List<ChatMessageResponse> messages
) {
    public static ChatHistoryResponse fromDomain(ChatChannel channel) {
        List<ChatMessageResponse> msgDtos = channel.getMessages()
                .stream()
                .map(ChatMessageResponse::fromDomain)
                .toList();

        return new ChatHistoryResponse(
                channel.getId().value(),
                channel.getType().name(),
                channel.getReferenceId(),
                msgDtos
        );
    }
}