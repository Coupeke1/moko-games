package be.kdg.team22.gamesservice.domain;

import org.jmolecules.ddd.annotation.Repository;

import java.util.Optional;

@Repository
public interface GameRepository {
    Optional<Game> findById(GameId id);
}