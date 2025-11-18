package be.kdg.team22.userservice.profile.api.profile;

import be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa.JpaProfileRepository;
import be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa.ProfileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JpaProfileRepository jpaRepo;

    @BeforeEach
    void setup() {
        jpaRepo.deleteAll();
    }

    private Jwt jwt(String sub, String username, String email) {
        return Jwt.withTokenValue("dummy-token")
                .header("alg", "none")
                .claim("sub", sub)
                .claim("preferred_username", username)
                .claim("email", email)
                .build();
    }

    @Test
    void firstCallCreatesProfile() throws Exception {
        String keycloakId = "11111111-1111-1111-1111-111111111111";

        mockMvc.perform(
                        get("/api/profiles/me")
                                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(
                                        jwt(keycloakId, "jan", "jan@kdg.be")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value(keycloakId))
                .andExpect(jsonPath("$.username").value("jan"))
                .andExpect(jsonPath("$.email").value("jan@kdg.be"));

        assert jpaRepo.findById(UUID.fromString(keycloakId)).isPresent();
    }

    @Test
    void requestWithoutJwtShouldFail() throws Exception {
        mockMvc.perform(get("/api/profiles/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void existingProfileIsReturned() throws Exception {
        String keycloakId = "22222222-2222-2222-2222-222222222222";

        jpaRepo.save(new ProfileEntity(
                UUID.fromString(keycloakId),
                "piet",
                "piet@kdg.be",
                Instant.now()
        ));

        mockMvc.perform(
                        get("/api/profiles/me")
                                .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(
                                        jwt(keycloakId, "piet", "piet@kdg.be")
                                ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("piet"));
    }

    @Test
    void invalidUuidReturns400() throws Exception {
        mockMvc.perform(get("/api/profiles/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfileByIdNotFoundReturns404() throws Exception {
        UUID id = UUID.fromString("44444444-4444-4444-4444-444444444444");

        mockMvc.perform(get("/api/profiles/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void multipleCallsAreIdempotent() throws Exception {
        String keycloakId = "55555555-5555-5555-5555-555555555555";

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(
                            get("/api/profiles/me")
                                    .with(SecurityMockMvcRequestPostProcessors.jwt().jwt(
                                            jwt(keycloakId, "rob", "rob@kdg.be")
                                    ))
                    )
                    .andExpect(status().isOk());
        }

        assert jpaRepo.count() == 1;
    }
}
