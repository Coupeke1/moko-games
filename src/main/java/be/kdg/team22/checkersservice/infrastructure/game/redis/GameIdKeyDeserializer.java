package be.kdg.team22.checkersservice.infrastructure.game.redis;

import be.kdg.team22.checkersservice.domain.game.GameId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class GameIdKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) {
        return GameId.fromString(key);
    }
}
