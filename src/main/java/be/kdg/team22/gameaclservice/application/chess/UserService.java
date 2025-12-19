package be.kdg.team22.gameaclservice.application.chess;

import be.kdg.team22.gameaclservice.infrastructure.user.ExternalUserRepository;
import be.kdg.team22.gameaclservice.infrastructure.user.UserResponse;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final ExternalUserRepository userRepository;

    public UserService(ExternalUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<UUID, String> resolveUsernames(Collection<UUID> userIds) {
        return userIds.stream()
                .map(userRepository::getUser)
                .collect(Collectors.toMap(
                        UserResponse::id,
                        UserResponse::username
                ));
    }
}
