package be.kdg.team22.sessionservice.application.friends;

import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.friends.ExternalFriendsRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FriendsService {
    private final ExternalFriendsRepository repository;

    public FriendsService(final ExternalFriendsRepository repository) {
        this.repository = repository;
    }

    public List<PlayerId> findAllFriends(final Jwt token) {
        List<UUID> response = repository.getFriendIds(token.getTokenValue());
        return response.stream().map(PlayerId::new).toList();
    }
}