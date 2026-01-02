package be.kdg.team22.communicationservice.infrastructure.chat;

import be.kdg.team22.communicationservice.api.chat.models.message.MessageModel;

import java.util.UUID;

public record ChatMessage(UUID userId,
                          String queue,
                          MessageModel payload) {}
