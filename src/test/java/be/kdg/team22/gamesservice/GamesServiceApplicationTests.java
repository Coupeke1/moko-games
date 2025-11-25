package be.kdg.team22.gamesservice;

import be.kdg.team22.gamesservice.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfig.class)
@SpringBootTest
class GamesServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}