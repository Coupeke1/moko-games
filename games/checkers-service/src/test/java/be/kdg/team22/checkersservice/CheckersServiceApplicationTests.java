package be.kdg.team22.checkersservice;

import be.kdg.team22.checkersservice.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "bot-service.upload-on-startup=false")
@Import(TestcontainersConfig.class)
class CheckersServiceApplicationTests {
    @Test
    void contextLoads() {
    }
}