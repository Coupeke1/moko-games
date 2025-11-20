package be.kdg.team22.userservice;

import be.kdg.team22.userservice.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfig.class)
class UserServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
