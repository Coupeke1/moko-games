package be.kdg.team22.sessionservice.application.lobby;

import be.kdg.team22.sessionservice.application.friends.FriendsService;
import be.kdg.team22.sessionservice.application.player.PlayerService;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyRepository;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.exceptions.PlayerNotFriendException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class LobbyPlayerService {
    private final LobbyRepository lobbyRepository;
    private final FriendsService friendsService;
    private final PlayerService playerService;
    private final LobbyPublisherService lobbyPublisher;

    public LobbyPlayerService(final LobbyRepository lobbyRepository, final FriendsService friendsService, final PlayerService playerService, final LobbyPublisherService lobbyPublisher) {
        this.lobbyRepository = lobbyRepository;
        this.friendsService = friendsService;
        this.playerService = playerService;
        this.lobbyPublisher = lobbyPublisher;
    }

    public void invitePlayer(final PlayerId ownerId, final LobbyId lobbyId, final PlayerId playerId, final Jwt token) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(lobbyId::notFound);
        ensureFriend(ownerId, playerId, token);

        lobby.invitePlayer(ownerId, playerId);
        lobbyPublisher.saveAndPublish(lobby);
    }

    public void addBot(final PlayerId ownerId, final LobbyId lobbyId) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(lobbyId::notFound);

        Player bot = playerService.createBot();

        lobby.addBot(ownerId, bot);
        lobbyPublisher.saveAndPublish(lobby);
    }

    public void removeBot(final PlayerId ownerId, final LobbyId lobbyId) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(lobbyId::notFound);

        lobby.removeBot(ownerId);
        lobbyPublisher.saveAndPublish(lobby);
    }

    public void acceptInvite(final PlayerId playerId, final LobbyId lobbyId, final Jwt token) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(lobbyId::notFound);
        Player player = playerService.findPlayer(playerId, token);

        lobby.acceptInvite(player);
        lobbyPublisher.saveAndPublish(lobby);
    }

    public void removePlayer(final PlayerId ownerId, final LobbyId lobbyId, final PlayerId playerId) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(lobbyId::notFound);

        lobby.removePlayer(ownerId, playerId);
        lobbyPublisher.saveAndPublish(lobby);
    }

    private void ensureFriend(final PlayerId ownerId, final PlayerId playerId, final Jwt token) {
        List<PlayerId> friends = friendsService.findAllFriends(token);

        if (!friends.contains(playerId))
            throw new PlayerNotFriendException(ownerId, playerId);
    }

    public void setReady(final PlayerId playerId, final LobbyId lobbyId) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(lobbyId::notFound);

        lobby.setReady(playerId);
        lobbyPublisher.saveAndPublish(lobby);
    }

    public void setUnready(final PlayerId playerId, final LobbyId lobbyId) {
        Lobby lobby = lobbyRepository.findById(lobbyId).orElseThrow(lobbyId::notFound);

        lobby.setUnready(playerId);
        lobbyPublisher.saveAndPublish(lobby);
    }
}