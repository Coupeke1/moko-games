package be.kdg.team22.userservice.profile.api.profile;

import be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa.JpaProfileRepository;
import be.kdg.team22.userservice.profile.infrastructure.db.repositories.profile.jpa.ProfileEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static be.kdg.team22.userservice.testutils.JwtTestUtils.jwtWithUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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

    @Test
    void firstCallCreatesProfile() throws Exception {
        String keycloakId = "11111111-1111-1111-1111-111111111111";

        mockMvc.perform(
                        get("/api/profiles/me")
                                .with(jwtWithUser(keycloakId, "jan", "jan@kdg.be"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(keycloakId))
                .andExpect(jsonPath("$.username").value("jan"))
                .andExpect(jsonPath("$.email").value("jan@kdg.be"));

        assertThat(jpaRepo.findById(UUID.fromString(keycloakId))).isPresent();
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
                                .with(jwtWithUser(keycloakId, "piet", "piet@kdg.be"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("piet"));
    }

    @Test
    void multipleCallsAreIdempotent() throws Exception {
        String keycloakId = "33333333-3333-3333-3333-333333333333";

        for (int i = 0; i < 5; i++) {
            mockMvc.perform(
                    get("/api/profiles/me").with(jwtWithUser(keycloakId, "rob", "mail@mail"))
            ).andExpect(status().isOk());
        }

        assertThat(jpaRepo.count()).isEqualTo(1);
    }

    @Test
    void invalidUuidReturns400() throws Exception {
        mockMvc.perform(get("/api/profiles/not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfileByIdNotFoundReturns404() throws Exception {
        mockMvc.perform(get("/api/profiles/44444444-4444-4444-4444-444444444444"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMyProfile_updatesValuesCorrectly() throws Exception {
        String id = "55555555-5555-5555-5555-555555555555";

        jpaRepo.save(new ProfileEntity(
                UUID.fromString(id),
                "oldname",
                "old@mail",
                Instant.now()
        ));

        mockMvc.perform(
                        patch("/api/profiles/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "newname",
                                            "email": "new@mail.com"
                                        }
                                        """)
                                .with(jwtWithUser(id, "irrelevant", "irrelevant"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("newname"))
                .andExpect(jsonPath("$.email").value("new@mail.com"));
    }

    @Test
    void updateProfile_failsWhenMissingJwt() throws Exception {
        mockMvc.perform(
                patch("/api/profiles/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username": "x", "email": "x@mail.com"}
                                """)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void updateProfile_invalidEmailReturns400() throws Exception {
        String id = "66666666-6666-6666-6666-666666666666";

        jpaRepo.save(new ProfileEntity(
                UUID.fromString(id),
                "name",
                "mail@mail",
                Instant.now()
        ));

        mockMvc.perform(
                        patch("/api/profiles/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "okname",
                                            "email": "NOT_AN_EMAIL"
                                        }
                                        """)
                                .with(jwtWithUser(id, "user", "user@mail"))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProfile_missingFieldsReturns400() throws Exception {
        String id = "77777777-7777-7777-7777-777777777777";

        jpaRepo.save(new ProfileEntity(
                UUID.fromString(id),
                "name",
                "mail@mail",
                Instant.now()
        ));

        mockMvc.perform(
                        patch("/api/profiles/me")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        { "username": "" }
                                        """)
                                .with(jwtWithUser(id, "user", "user@mail"))
                )
                .andExpect(status().isBadRequest());
    }
}
