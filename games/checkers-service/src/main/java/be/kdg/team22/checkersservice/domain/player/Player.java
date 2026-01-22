package be.kdg.team22.checkersservice.domain.player;

public record Player(PlayerId id,
                     PlayerRole role,
                     boolean botPlayer) {
}

