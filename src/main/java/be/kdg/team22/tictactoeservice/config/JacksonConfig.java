package be.kdg.team22.tictactoeservice.config;

import be.kdg.team22.tictactoeservice.domain.game.GameId;
import be.kdg.team22.tictactoeservice.domain.player.PlayerId;
import be.kdg.team22.tictactoeservice.infrastructure.game.redis.GameIdKeyDeserializer;
import be.kdg.team22.tictactoeservice.infrastructure.game.redis.PlayerIdKeyDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public SimpleModule idKeyModule() {
        SimpleModule module = new SimpleModule();
        module.addKeyDeserializer(PlayerId.class, new PlayerIdKeyDeserializer());
        module.addKeyDeserializer(GameId.class, new GameIdKeyDeserializer());
        return module;
    }
}
