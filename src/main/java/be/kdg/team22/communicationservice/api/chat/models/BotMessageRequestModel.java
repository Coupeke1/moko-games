package be.kdg.team22.communicationservice.api.chat.models;

public record BotMessageRequestModel(
        String content,
        String gameName
) {
}