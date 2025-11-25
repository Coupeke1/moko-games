package be.kdg.team22.userservice.application.profile;

import be.kdg.team22.userservice.domain.profile.*;
import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import be.kdg.team22.userservice.infrastructure.image.ExternalImageRepository;
import be.kdg.team22.userservice.infrastructure.image.ImageResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ProfileServiceTest {
    private final ProfileRepository profileRepository = mock(ProfileRepository.class);
    private final ExternalImageRepository imageRepository = mock(ExternalImageRepository.class);
    private final ProfileService service = new ProfileService(profileRepository, imageRepository);

    private Jwt token(String sub, String username, String email) {
        return Jwt.withTokenValue("dummy").header("alg", "none").subject(sub).claim("preferred_username", username).claim("email", email).build();
    }

    @Test
    void createsProfileWhenNotExists() {
        String sub = "11111111-1111-1111-1111-111111111111";
        Jwt token = token(sub, "jan", "jan@kdg.be");
        ProfileId id = new ProfileId(UUID.fromString(sub));

        when(imageRepository.get()).thenReturn(new ImageResponse("test", "image", 200, 200));
        when(profileRepository.findById(id)).thenReturn(Optional.empty());

        Profile profile = service.getOrCreate(token);

        assertThat(profile.id().value()).isEqualTo(UUID.fromString(sub));

        ArgumentCaptor<Profile> saved = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(saved.capture());
    }

    @Test
    void returnsExistingProfile() {
        String sub = "22222222-2222-2222-2222-222222222222";
        ProfileId id = new ProfileId(UUID.fromString(sub));
        ProfileName username = new ProfileName("existing-user");
        ProfileEmail email = new ProfileEmail("user@existing.com");
        String description = "Test";
        Profile existing = new Profile(id, username, email, description, "");

        when(imageRepository.get()).thenReturn(new ImageResponse("test", "image", 200, 200));
        when(profileRepository.findById(id)).thenReturn(Optional.of(existing));

        Profile result = service.getOrCreate(token(sub, "anderenaam", "andere@mail"));

        assertThat(result.email()).isSameAs(existing.email());
        assertThat(result.username()).isSameAs(existing.username());
        assertThat(result.description()).isSameAs(existing.description());
        verify(profileRepository, never()).save(any());
    }

    @Test
    void missingSubjectThrows() {
        Jwt jwt = Jwt.withTokenValue("dummy").header("alg", "none").claim("email", "jan@kdg.be").claim("preferred_username", "jan").build();
        assertThatThrownBy(() -> service.getOrCreate(jwt)).isInstanceOf(ClaimNotFoundException.class);
    }
}