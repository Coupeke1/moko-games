package be.kdg.team22.communicationservice.utils;

import org.springframework.security.oauth2.jwt.Jwt;
import java.time.Instant;
import java.util.Map;

public class TestJwtUtils {

    public static Jwt jwtFor(String userId) {
        return new Jwt(
                "test-token",
                Instant.now(),
                Instant.now().plusSeconds(3600),
                Map.of("alg", "none"),
                Map.of("sub", userId)
        );
    }
}
