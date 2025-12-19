package be.kdg.team22.gameaclservice.domain;

import org.jmolecules.ddd.annotation.AggregateRoot;

import java.util.List;
import java.util.UUID;

@AggregateRoot
public class Game {
    UUID id;
    List<UUID> players;

    private Game (UUID id, List<UUID> players) {
        this.id = id;
        this.players = players;
    }

    public static Game create(List<UUID> players) {
        return new Game(UUID.randomUUID(), players);
    }

    public UUID id() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<UUID> players() {
        return players;
    }
}
