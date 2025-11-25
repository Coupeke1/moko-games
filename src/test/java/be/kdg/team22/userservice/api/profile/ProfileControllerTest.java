package be.kdg.team22.userservice.api.profile;

import be.kdg.team22.userservice.infrastructure.profile.jpa.JpaProfileRepository;
import be.kdg.team22.userservice.infrastructure.profile.jpa.ModulesEmbed;
import be.kdg.team22.userservice.infrastructure.profile.jpa.ProfileEntity;
import be.kdg.team22.userservice.infrastructure.profile.jpa.StatisticsEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static be.kdg.team22.userservice.utils.Token.tokenWithUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfileControllerTest {
    @Autowired
    private MockMvc mock;

    @Autowired
    private JpaProfileRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void firstCallCreatesProfile() throws Exception {
        String id = "11111111-1111-1111-1111-111111111111";
        mock.perform(get("/api/profiles/me").with(tokenWithUser(id, "jan", "jan@kdg.be"))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));

        assertThat(repository.findById(UUID.fromString(id))).isPresent();
    }

    @Test
    void requestWithoutJwtShouldFail() throws Exception {
        mock.perform(get("/api/profiles/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void existingProfileIsReturned() throws Exception {
        String id = "22222222-2222-2222-2222-222222222222";
        String username = "user";
        String email = "user@email.com";
        String description = "Test";
        StatisticsEmbed statistics = new StatisticsEmbed(5, 50);
        ModulesEmbed modules = new ModulesEmbed(true, false);

        repository.save(new ProfileEntity(UUID.fromString(id), username, email, description, "", statistics, modules, Instant.now()));
        mock.perform(get("/api/profiles/me").with(tokenWithUser(id, "piet", "piet@kdg.be"))).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void multipleCallsAreIdempotent() throws Exception {
        String id = "33333333-3333-3333-3333-333333333333";

        for (int i = 0; i < 5; i++) {
            mock.perform(get("/api/profiles/me").with(tokenWithUser(id, "rob", "mail@mail"))).andExpect(status().isOk());
        }

        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void invalidUuidReturns400() throws Exception {
        mock.perform(get("/api/profiles/not-a-uuid")).andExpect(status().isInternalServerError());
    }

    @Test
    void getProfileByIdNotFoundReturns404() throws Exception {
        mock.perform(get("/api/profiles/44444444-4444-4444-4444-444444444444")).andExpect(status().isNotFound());
    }
}