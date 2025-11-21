package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.*;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.PlayerNotFriendException;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.friends.ExternalFriendsRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.users.ExternalUserRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.users.UserResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class LobbyPlayerService {

    private final LobbyRepository lobbyRepository;
    private final ExternalFriendsRepository friendsRepository;
    private final ExternalUserRepository userRepository;

    public LobbyPlayerService(
            LobbyRepository lobbyRepository,
            ExternalFriendsRepository friendsRepository,
            ExternalUserRepository userRepository
    ) {
        this.lobbyRepository = lobbyRepository;
        this.friendsRepository = friendsRepository;
        this.userRepository = userRepository;
    }

    public void invitePlayer(PlayerId owner, LobbyId lobbyId, PlayerId target, Jwt token) {
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId.value()));

        ensureFriend(owner, target, token);

        lobby.invitePlayer(owner, target);
        lobbyRepository.save(lobby);
    }

    public void invitePlayers(PlayerId owner, LobbyId lobbyId, List<PlayerId> targets, Jwt token) {
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId.value()));

        List<PlayerId> friends = friendsRepository.getFriendIds(token).stream()
                .map(PlayerId::from)
                .toList();

        Set<PlayerId> friendSet = Set.copyOf(friends);

        for (PlayerId t : targets) {
            if (!friendSet.contains(t)) {
                throw new PlayerNotFriendException(owner, t.value());
            }
        }

        lobby.invitePlayers(owner, targets);
        lobbyRepository.save(lobby);
    }

    public void acceptInvite(PlayerId userId, LobbyId lobbyId, String token) {
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId.value()));

        UserResponse response = userRepository.getById(userId.value(), token);
        LobbyPlayer domainPlayer = new LobbyPlayer(response.id(), response.username());

        lobby.acceptInvite(domainPlayer);
        lobbyRepository.save(lobby);
    }

    public void removePlayer(PlayerId owner, LobbyId lobbyId, PlayerId target) {
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId.value()));

        lobby.removePlayer(owner, target);
        lobbyRepository.save(lobby);
    }

    public void removePlayers(PlayerId owner, LobbyId lobbyId, List<PlayerId> ids) {
        Lobby lobby = lobbyRepository.findById(lobbyId)
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId.value()));

        lobby.removePlayers(owner, ids);
        lobbyRepository.save(lobby);
    }

    private void ensureFriend(PlayerId owner, PlayerId target, Jwt token) {
        List<PlayerId> friends = friendsRepository.getFriendIds(token).stream()
                .map(PlayerId::from)
                .toList();

        if (!friends.contains(target))
            throw new PlayerNotFriendException(owner, target.value());
    }
}