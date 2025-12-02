package be.kdg.team22.tictactoeservice.infrastructure.game.redis;

import be.kdg.team22.tictactoeservice.domain.game.GameId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class GameIdKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) {
        return GameId.fromString(key);
    }
}
