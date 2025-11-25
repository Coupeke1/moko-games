package be.kdg.team22.gamesservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class GamesServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
