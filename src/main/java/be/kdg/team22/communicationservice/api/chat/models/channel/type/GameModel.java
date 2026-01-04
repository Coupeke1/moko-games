package be.kdg.team22.communicationservice.api.chat.models.channel.type;

import java.util.UUID;

public record GameModel(UUID id, String title,
                        String description) {}