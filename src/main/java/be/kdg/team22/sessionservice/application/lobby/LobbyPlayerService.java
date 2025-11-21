package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyPlayer;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.LobbyNotFoundException;
import be.kdg.team22.sessionservice.domain.lobby.exceptions.PlayerNotFriendException;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.friends.ExternalFriendsRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.users.ExternalUserRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.users.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    public void invitePlayer(UUID ownerId, UUID lobbyId, UUID targetPlayerId) {
        Lobby lobby = lobbyRepository.findById(new LobbyId(lobbyId))
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId));

        ensureFriend(ownerId, targetPlayerId);

        lobby.invitePlayer(ownerId, targetPlayerId);
        lobbyRepository.save(lobby);
    }

    public void invitePlayers(UUID ownerId, UUID lobbyId, List<UUID> targetPlayerIds) {
        Lobby lobby = lobbyRepository.findById(new LobbyId(lobbyId))
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId));

        List<UUID> friendIds = friendsRepository.getFriendIds(ownerId);
        Set<UUID> friendSet = Set.copyOf(friendIds);

        boolean allAreFriends = friendSet.containsAll(targetPlayerIds);
        if (!allAreFriends) {
            throw new PlayerNotFriendException(ownerId, findFirstNonFriend(targetPlayerIds, friendSet));
        }

        lobby.invitePlayers(ownerId, targetPlayerIds);
        lobbyRepository.save(lobby);
    }

    public void acceptInvite(UUID currentUserId, UUID lobbyId) {
        Lobby lobby = lobbyRepository.findById(new LobbyId(lobbyId))
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId));

        UserResponse user = userRepository.getById(currentUserId);
        LobbyPlayer player = new LobbyPlayer(user.id(), user.username());

        lobby.acceptInvite(player);
        lobbyRepository.save(lobby);
    }

    public void removePlayer(UUID ownerId, UUID lobbyId, UUID targetPlayerId) {
        Lobby lobby = lobbyRepository.findById(new LobbyId(lobbyId))
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId));

        lobby.removePlayer(ownerId, targetPlayerId);
        lobbyRepository.save(lobby);
    }

    public void removePlayers(UUID ownerId, UUID lobbyId, List<UUID> targetPlayerIds) {
        Lobby lobby = lobbyRepository.findById(new LobbyId(lobbyId))
                .orElseThrow(() -> new LobbyNotFoundException(lobbyId));

        lobby.removePlayers(ownerId, targetPlayerIds);
        lobbyRepository.save(lobby);
    }

    private void ensureFriend(UUID ownerId, UUID targetPlayerId) {
        List<UUID> friendIds = friendsRepository.getFriendIds(ownerId);
        if (!friendIds.contains(targetPlayerId)) {
            throw new PlayerNotFriendException(ownerId, targetPlayerId);
        }
    }

    private UUID findFirstNonFriend(List<UUID> candidates, Set<UUID> friendSet) {
        return candidates.stream()
                .filter(id -> !friendSet.contains(id))
                .findFirst()
                .orElseThrow();
    }
}
