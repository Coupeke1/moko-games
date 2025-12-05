package be.kdg.team22.communicationservice.infrastructure.lobby;

import java.util.UUID;

public record PlayerModel(UUID id,
                          String username,
                          String image,
                          boolean ready) {
}