package be.kdg.team22.gameaclservice.application.chess;

import be.kdg.team22.gameaclservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.gameaclservice.infrastructure.user.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {
    @Mock
    private ExternalUserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("should resolve single user id to username mapping")
    void testResolveUsernamesWithSingleUser() {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse(userId, "Alice");

        when(userRepository.getUser(userId)).thenReturn(userResponse);

        Map<UUID, String> result = userService.resolveUsernames(List.of(userId));

        assertThat(result)
                .hasSize(1)
                .containsEntry(userId, "Alice");
    }

    @Test
    @DisplayName("should resolve multiple user ids to username mappings")
    void testResolveUsernamesWithMultipleUsers() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID userId3 = UUID.randomUUID();

        UserResponse response1 = new UserResponse(userId1, "Alice");
        UserResponse response2 = new UserResponse(userId2, "Bob");
        UserResponse response3 = new UserResponse(userId3, "Charlie");

        when(userRepository.getUser(userId1)).thenReturn(response1);
        when(userRepository.getUser(userId2)).thenReturn(response2);
        when(userRepository.getUser(userId3)).thenReturn(response3);

        Map<UUID, String> result = userService.resolveUsernames(List.of(userId1, userId2, userId3));

        assertThat(result)
                .hasSize(3)
                .containsEntry(userId1, "Alice")
                .containsEntry(userId2, "Bob")
                .containsEntry(userId3, "Charlie");
    }

    @Test
    @DisplayName("should return empty map for empty user id collection")
    void testResolveUsernamesWithEmptyCollection() {
        Map<UUID, String> result = userService.resolveUsernames(List.of());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should handle usernames with special characters")
    void testResolveUsernamesWithSpecialCharacters() {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse(userId, "user_with-special.chars");

        when(userRepository.getUser(userId)).thenReturn(userResponse);

        Map<UUID, String> result = userService.resolveUsernames(List.of(userId));

        assertThat(result)
                .containsEntry(userId, "user_with-special.chars");
    }

    @Test
    @DisplayName("should preserve exact username casing")
    void testResolveUsernamesPreservesCasing() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        when(userRepository.getUser(userId1)).thenReturn(new UserResponse(userId1, "UPPERCASE"));
        when(userRepository.getUser(userId2)).thenReturn(new UserResponse(userId2, "lowercase"));

        Map<UUID, String> result = userService.resolveUsernames(List.of(userId1, userId2));

        assertThat(result)
                .containsEntry(userId1, "UPPERCASE")
                .containsEntry(userId2, "lowercase");
    }

    @Test
    @DisplayName("should create mapping using provided user ids as keys")
    void testResolveUsernamesUsesUserIdAsMapKey() {
        UUID userId = UUID.randomUUID();
        String username = "TestUser";
        UserResponse userResponse = new UserResponse(userId, username);

        when(userRepository.getUser(userId)).thenReturn(userResponse);

        Map<UUID, String> result = userService.resolveUsernames(List.of(userId));

        assertThat(result.keySet()).containsExactly(userId);
        assertThat(result.get(userId)).isEqualTo(username);
    }

    @Test
    @DisplayName("should handle numeric usernames")
    void testResolveUsernamesWithNumericUsernames() {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse(userId, "12345");

        when(userRepository.getUser(userId)).thenReturn(userResponse);

        Map<UUID, String> result = userService.resolveUsernames(List.of(userId));

        assertThat(result).containsEntry(userId, "12345");
    }

    @Test
    @DisplayName("should handle usernames with spaces")
    void testResolveUsernamesWithSpaces() {
        UUID userId = UUID.randomUUID();
        UserResponse userResponse = new UserResponse(userId, "John Doe");

        when(userRepository.getUser(userId)).thenReturn(userResponse);

        Map<UUID, String> result = userService.resolveUsernames(List.of(userId));

        assertThat(result).containsEntry(userId, "John Doe");
    }

    @Test
    @DisplayName("should map all provided user ids in result")
    void testResolveUsernamesMapAllUserIds() {
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();
        UUID userId3 = UUID.randomUUID();

        when(userRepository.getUser(userId1)).thenReturn(new UserResponse(userId1, "User1"));
        when(userRepository.getUser(userId2)).thenReturn(new UserResponse(userId2, "User2"));
        when(userRepository.getUser(userId3)).thenReturn(new UserResponse(userId3, "User3"));

        Map<UUID, String> result = userService.resolveUsernames(List.of(userId1, userId2, userId3));

        assertThat(result.keySet())
                .containsExactlyInAnyOrder(userId1, userId2, userId3);
    }

    @Test
    @DisplayName("should resolve usernames from stream operation")
    void testResolveUsernamesWorksWithLargeCollection() {
        int userCount = 10;
        List<UUID> userIds = List.of(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID()
        );

        userIds.forEach(id -> when(userRepository.getUser(id)).thenReturn(new UserResponse(id, "User")));

        Map<UUID, String> result = userService.resolveUsernames(userIds);

        assertThat(result).hasSize(userCount);
    }
}
