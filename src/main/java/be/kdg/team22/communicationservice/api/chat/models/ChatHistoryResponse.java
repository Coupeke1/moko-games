package be.kdg.team22.communicationservice.api.chat.models;

import be.kdg.team22.communicationservice.domain.chat.Channel;

import java.util.List;
import java.util.UUID;

public record ChatHistoryResponse(
        UUID channelId,
        String type,
        String referenceId,
        List<ChatMessageResponse> messages
) {
    public static ChatHistoryResponse fromDomain(Channel channel) {
        List<ChatMessageResponse> msgDtos = channel.getMessages()
                .stream()
                .map(ChatMessageResponse::from)
                .toList();

        return new ChatHistoryResponse(
                channel.getId().value(),
                channel.getType().name(),
                channel.getReferenceId(),
                msgDtos
        );
    }
}