package be.kdg.team22.userservice.testutils;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public final class JwtTestUtils {

    private JwtTestUtils() {
    }

    public static RequestPostProcessor jwtWithUser(String sub, String username, String email) {
        return jwt().jwt(builder -> {
            builder.subject(sub);
            builder.claim("preferred_username", username);
            builder.claim("email", email);
            builder.claim("realm_access", Map.of("roles", List.of("USER")));
        });
    }
}
