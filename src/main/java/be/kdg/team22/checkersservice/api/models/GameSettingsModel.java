package be.kdg.team22.checkersservice.api.models;

import be.kdg.team22.checkersservice.domain.move.KingMovementMode;

public record GameSettingsModel(KingMovementMode kingMovementMode) {
    public GameSettingsModel {
        if (kingMovementMode == null) {
            kingMovementMode = KingMovementMode.SINGLE;
        }
    }
}
