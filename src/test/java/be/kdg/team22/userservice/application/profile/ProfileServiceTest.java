package be.kdg.team22.userservice.application.profile;

import be.kdg.team22.userservice.domain.profile.Profile;
import be.kdg.team22.userservice.domain.profile.ProfileId;
import be.kdg.team22.userservice.domain.profile.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.NoSuchElementException;
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
        assertThat(profile.username()).isEqualTo("jan");
        assertThat(profile.email()).isEqualTo("jan@kdg.be");

        ArgumentCaptor<Profile> saved = ArgumentCaptor.forClass(Profile.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().username()).isEqualTo("jan");
    }

    @Test
    void returnsExistingProfile() {
        String sub = "22222222-2222-2222-2222-222222222222";
        ProfileId id = new ProfileId(UUID.fromString(sub));
        Profile existing = new Profile(id, "piet", "piet@kdg.be", Instant.now());

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        Profile result = service.getOrCreate(token(sub, "anderenaam", "andere@mail"));

        assertThat(result).isSameAs(existing);
        verify(repository, never()).save(any());
    }

    @Test
    void missingSubjectThrows() {
        Jwt jwt = Jwt.withTokenValue("dummy").header("alg", "none").claim("email", "jan@kdg.be").claim("preferred_username", "jan").build();
        assertThatThrownBy(() -> service.getOrCreate(jwt)).isInstanceOf(MissingRequiredClaimException.class).hasMessageContaining("sub");
    }

    @Test
    void missingEmailThrows() {
        Jwt jwt = Jwt.withTokenValue("dummy").header("alg", "none").subject("33333333-3333-3333-3333-333333333333").claim("preferred_username", "jan").build();
        assertThatThrownBy(() -> service.getOrCreate(jwt)).isInstanceOf(MissingRequiredClaimException.class).hasMessageContaining("email");
    }

    @Test
    void updateUpdatesProfile() {
        String sub = "99999999-9999-9999-9999-999999999999";
        ProfileId id = new ProfileId(UUID.fromString(sub));

        Profile existing = new Profile(id, "old", "old@mail", Instant.now());

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(repository).save(any(Profile.class));

        Jwt token = token(sub, "ignored", "ignored@mail");
        Profile updated = service.update(token, "newname", "new@mail");

        assertThat(updated.username()).isEqualTo("newname");
        assertThat(updated.email()).isEqualTo("new@mail");
    }

    @Test
    void updateThrowsWhenProfileNotExists() {
        String sub = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";
        ProfileId id = new ProfileId(UUID.fromString(sub));

        when(repository.findById(id)).thenReturn(Optional.empty());
        Jwt token = token(sub, "ignored", "ignored@mail");

        assertThatThrownBy(() -> service.update(token, "x", "x@mail")).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void updateMissingSubjectThrows() {
        Jwt token = Jwt.withTokenValue("dummy").header("alg", "none").claim("email", "mail@mail").claim("preferred_username", "user").build();
        assertThatThrownBy(() -> service.update(token, "x", "mail@mail")).isInstanceOf(MissingRequiredClaimException.class).hasMessageContaining("sub");
    }
}