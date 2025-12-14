package be.kdg.team22.userservice.application.profile;

import be.kdg.team22.userservice.domain.achievement.AchievementRepository;
import be.kdg.team22.userservice.domain.library.LibraryRepository;
import be.kdg.team22.userservice.domain.profile.*;
import be.kdg.team22.userservice.domain.profile.exceptions.CannotUpdateProfileException;
import be.kdg.team22.userservice.domain.profile.exceptions.ClaimNotFoundException;
import be.kdg.team22.userservice.domain.profile.exceptions.ProfileNotFoundException;
import be.kdg.team22.userservice.infrastructure.games.ExternalGamesRepository;
import be.kdg.team22.userservice.infrastructure.image.ExternalImageRepository;
import be.kdg.team22.userservice.infrastructure.image.ImageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private final ProfileRepository repo = mock(ProfileRepository.class);
    private final ExternalImageRepository imageRepo = mock(ExternalImageRepository.class);
    private final AchievementRepository achievementRepo = mock(AchievementRepository.class);
    private final LibraryRepository libraryRepo = mock(LibraryRepository.class);
    private final ExternalGamesRepository gameRepo = mock(ExternalGamesRepository.class);
    private final ProfileService service = new ProfileService(repo, imageRepo, achievementRepo, libraryRepo, gameRepo);

    private Jwt token(String sub, String username, String email) {
        return Jwt.withTokenValue("dummy")
                .header("alg", "none")
                .subject(sub)
                .claim("preferred_username", username)
                .claim("email", email)
                .build();
    }

    private Jwt tokenNoUsername(String sub, String email) {
        return Jwt.withTokenValue("dummy")
                .header("alg", "none")
                .subject(sub)
                .claim("email", email)
                .build();
    }

    private Jwt tokenNoEmail(String sub, String username) {
        return Jwt.withTokenValue("dummy")
                .header("alg", "none")
                .subject(sub)
                .claim("preferred_username", username)
                .build();
    }

    @Test
    @DisplayName("getOrCreate → creates new profile when not exists")
    void createsProfileWhenNotExists() {
        String sub = "11111111-1111-1111-1111-111111111111";
        Jwt jwt = token(sub, "jan", "jan@kdg.be");
        ProfileId id = new ProfileId(UUID.fromString(sub));

        when(repo.findById(id)).thenReturn(Optional.empty());
        when(imageRepo.get()).thenReturn(new ImageResponse("avatar", "https://img", 100, 100));

        Profile profile = service.getOrCreate(jwt);

        assertThat(profile.id().value()).isEqualTo(UUID.fromString(sub));
        assertThat(profile.username().value()).isEqualTo("jan");
        assertThat(profile.email().value()).isEqualTo("jan@kdg.be");
        assertThat(profile.description()).isEqualTo("Hey there! I am using Moko.");

        ArgumentCaptor<Profile> saved = ArgumentCaptor.forClass(Profile.class);
        verify(repo).save(saved.capture());
        assertThat(saved.getValue().image()).isEqualTo("https://img");
    }

    @Test
    @DisplayName("getOrCreate → returns existing profile")
    void returnsExistingProfile() {
        String sub = "22222222-2222-2222-2222-222222222222";
        ProfileId id = new ProfileId(UUID.fromString(sub));
        Profile existing = new Profile(
                id,
                new ProfileName("existing"),
                new ProfileEmail("existing@mail"),
                "desc",
                "img"
        );

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(imageRepo.get()).thenReturn(new ImageResponse("url", "avatar", 100, 100));

        Profile result = service.getOrCreate(token(sub, "ignored", "ignored"));

        assertThat(result).isSameAs(existing);
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("getOrCreate → missing subject claims throws")
    void missingSubjectThrows() {
        Jwt jwt = Jwt.withTokenValue("dummy")
                .header("alg", "none")
                .claim("preferred_username", "jan")
                .claim("email", "jan@kdg.be")
                .build();

        assertThatThrownBy(() -> service.getOrCreate(jwt))
                .isInstanceOf(ClaimNotFoundException.class);
    }

    @Test
    @DisplayName("getById → returns found profile")
    void getById_success() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Profile profile = new Profile(id, new ProfileName("u"), new ProfileEmail("u@mail"),
                "desc", "img");

        when(repo.findById(id)).thenReturn(Optional.of(profile));

        assertThat(service.getById(id)).isSameAs(profile);
    }

    @Test
    @DisplayName("getById → throws when missing")
    void getById_notFound() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(ProfileNotFoundException.class);
    }

    @Test
    @DisplayName("getByUsername → success")
    void getByUsername_success() {
        ProfileName name = new ProfileName("jan");
        Profile profile = new Profile(new ProfileId(UUID.randomUUID()), name,
                new ProfileEmail("j@mail"), "desc", "img");

        when(repo.findByUsername(name)).thenReturn(Optional.of(profile));

        assertThat(service.getByUsername(name)).isSameAs(profile);
    }

    @Test
    @DisplayName("getByUsername → throws when missing")
    void getByUsername_notFound() {
        ProfileName name = new ProfileName("jan");
        when(repo.findByUsername(name)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByUsername(name))
                .isInstanceOf(ProfileNotFoundException.class);
    }

    @Test
    @DisplayName("changeDescription → updates successfully")
    void changeDescription_success() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Profile p = spy(new Profile(id, new ProfileName("u"), new ProfileEmail("e@mail"),
                "old", "img"));

        String updated = service.changeDescription(p, "new desc");

        assertThat(updated).isEqualTo("new desc");
        verify(p).updateDescription("new desc");
        verify(repo).save(p);
    }

    @Test
    @DisplayName("changeDescription → throws when same")
    void changeDescription_sameThrows() {
        ProfileId id = new ProfileId(UUID.randomUUID());
        Profile p = new Profile(id, new ProfileName("u"), new ProfileEmail("e@mail"),
                "old", "img");

        assertThatThrownBy(() -> service.changeDescription(p, "old"))
                .isInstanceOf(CannotUpdateProfileException.class);
    }

    @Test
    @DisplayName("changeImage → updates successfully")
    void changeImage_success() {
        Profile p = spy(new Profile(new ProfileId(UUID.randomUUID()),
                new ProfileName("u"), new ProfileEmail("e@mail"), "d", "oldImg"));

        String updated = service.changeImage(p, "newImg");

        assertThat(updated).isEqualTo("newImg");
        verify(p).updateImage("newImg");
        verify(repo).save(p);
    }

    @Test
    @DisplayName("changeImage → throws when same")
    void changeImage_sameThrows() {
        Profile p = new Profile(new ProfileId(UUID.randomUUID()),
                new ProfileName("u"), new ProfileEmail("e@mail"), "d", "same");

        assertThatThrownBy(() -> service.changeImage(p, "same"))
                .isInstanceOf(CannotUpdateProfileException.class);
    }

    @Test
    @DisplayName("changeModules → updates successfully")
    void changeModules_success() {
        Profile p = spy(new Profile(new ProfileId(UUID.randomUUID()),
                new ProfileName("u"), new ProfileEmail("e@mail"), "d", "i"));

        Modules newModules = new Modules(true, false);

        Modules updated = service.changeModules(p, newModules);

        assertThat(updated).isEqualTo(newModules);
        verify(p).updateModules(newModules);
        verify(repo).save(p);
    }

    @Test
    @DisplayName("changeModules → throws when same")
    void changeModules_sameThrows() {
        Modules modules = new Modules(true, true);
        Notifications notifications = new Notifications(false, true, false, true, true);
        Profile p = new Profile(new ProfileId(UUID.randomUUID()),
                new ProfileName("u"), new ProfileEmail("e@mail"), "d", "i",
                new Statistics(0, 0), modules, notifications, null);

        assertThatThrownBy(() -> service.changeModules(p, modules))
                .isInstanceOf(CannotUpdateProfileException.class);
    }

    @Test
    @DisplayName("getUsername → takes preferred_username first")
    void getUsername_preferred() {
        String sub = "11111111-1111-1111-1111-111111111111";
        Jwt jwt = token(sub, "mathias", "a@mail");

        when(repo.findById(new ProfileId(UUID.fromString(sub))))
                .thenReturn(Optional.empty());
        when(imageRepo.get()).thenReturn(new ImageResponse("url", "img", 100, 100));

        Profile result = service.getOrCreate(jwt);

        assertThat(result.username().value()).isEqualTo("mathias");
    }

    @Test
    @DisplayName("getUsername → falls back to 'username'")
    void getUsername_fallback() {
        String sub = "11111111-1111-1111-1111-111111111111";

        Jwt jwt = Jwt.withTokenValue("dummy")
                .header("alg", "none")
                .subject(sub)
                .claim("username", "fallback")
                .claim("email", "x@mail")
                .build();

        when(repo.findById(new ProfileId(UUID.fromString(sub)))).thenReturn(Optional.empty());
        when(imageRepo.get()).thenReturn(new ImageResponse("https://url", "img", 200, 200));

        ProfileName name = service.getOrCreate(jwt).username();

        assertThat(name.value()).isEqualTo("fallback");
    }

    @Test
    @DisplayName("getUsername → throws when missing")
    void getUsername_missing() {
        Jwt jwt = tokenNoUsername("11111111-1111-1111-1111-111111111111", "x@mail");

        assertThatThrownBy(() -> service.getOrCreate(jwt))
                .isInstanceOf(ClaimNotFoundException.class);
    }

    @Test
    @DisplayName("getEmail → throws when missing")
    void getEmail_missing() {
        Jwt jwt = tokenNoEmail("11111111-1111-1111-1111-111111111111", "mathias");

        assertThatThrownBy(() -> service.getOrCreate(jwt))
                .isInstanceOf(ClaimNotFoundException.class);
    }
}