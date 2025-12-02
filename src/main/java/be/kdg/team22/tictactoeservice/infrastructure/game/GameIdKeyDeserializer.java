package be.kdg.team22.tictactoeservice.infrastructure.game;

import be.kdg.team22.tictactoeservice.domain.game.GameId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

import java.io.IOException;

public class GameIdKeyDeserializer extends KeyDeserializer {
    @Override
    public Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        return GameId.fromString(key);
    }
}
