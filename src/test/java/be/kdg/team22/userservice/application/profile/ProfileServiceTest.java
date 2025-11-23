package be.kdg.team22.userservice.application.profile;

import be.kdg.team22.userservice.domain.profile.*;
import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProfileServiceTest {
    private final ProfileRepository repository = mock(ProfileRepository.class);
    private final ProfileService service = new ProfileService(repository);

    private Jwt token(String sub, String username, String email) {
        return Jwt.withTokenValue("dummy").header("alg", "none").subject(sub).claim("preferred_username", username).claim("email", email).build();
    }

    @Test
    void createsProfileWhenNotExists() {
        String sub = "11111111-1111-1111-1111-111111111111";
        Jwt token = token(sub, "jan", "jan@kdg.be");
        ProfileId id = new ProfileId(UUID.fromString(sub));

        when(repository.findById(id)).thenReturn(Optional.empty());

        Profile profile = service.getOrCreate(token);

        assertThat(profile.id().value()).isEqualTo(UUID.fromString(sub));

        ArgumentCaptor<Profile> saved = ArgumentCaptor.forClass(Profile.class);
        verify(repository).save(saved.capture());
    }

    @Test
    void returnsExistingProfile() {
        String sub = "22222222-2222-2222-2222-222222222222";
        ProfileId id = new ProfileId(UUID.fromString(sub));
        ProfileName username = new ProfileName("existing-user");
        ProfileEmail email = new ProfileEmail("user@existing.com");
        Profile existing = new Profile(id, username, email);

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        Profile result = service.getOrCreate(token(sub, "anderenaam", "andere@mail"));

        assertThat(result).isSameAs(existing);
        verify(repository, never()).save(any());
    }

    @Test
    void missingSubjectThrows() {
        Jwt jwt = Jwt.withTokenValue("dummy").header("alg", "none").claim("email", "jan@kdg.be").claim("preferred_username", "jan").build();
        assertThatThrownBy(() -> service.getOrCreate(jwt)).isInstanceOf(ClaimNotFoundException.class);
    }
}