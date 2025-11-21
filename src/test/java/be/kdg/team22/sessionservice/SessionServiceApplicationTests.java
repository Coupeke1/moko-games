package be.kdg.team22.sessionservice;

import be.kdg.team22.sessionservice.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.utility.TestcontainersConfiguration;

@Import(TestcontainersConfig.class)
@SpringBootTest
class SessionServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
