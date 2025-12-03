package be.kdg.team22.checkersservice.infrastructure.game.redis;

import be.kdg.team22.checkersservice.domain.player.PlayerId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.util.UUID;

public class PlayerIdKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) {
        return new PlayerId(UUID.fromString(key));
    }
}
