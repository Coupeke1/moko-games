package be.kdg.team22.tictactoeservice;

import be.kdg.team22.tictactoeservice.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest()
@Import(TestcontainersConfig.class)
public class TicTacToeServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}
