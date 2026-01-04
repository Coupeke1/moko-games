package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.channel.ChannelModel;
import be.kdg.team22.communicationservice.api.chat.models.channel.type.*;
import be.kdg.team22.communicationservice.api.chat.models.channel.type.exceptions.ConvertReferenceTypeException;
import be.kdg.team22.communicationservice.application.chat.ChannelService;
import be.kdg.team22.communicationservice.application.chat.GameService;
import be.kdg.team22.communicationservice.application.chat.LobbyService;
import be.kdg.team22.communicationservice.application.chat.UserService;
import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.GameId;
import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.LobbyReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.ReferenceType;
import be.kdg.team22.communicationservice.infrastructure.game.GameResponse;
import be.kdg.team22.communicationservice.infrastructure.lobby.models.PlayerModel;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/channel")
public class ChannelController {
    private final ChannelService channelService;
    private final UserService userService;
    private final LobbyService lobbyService;
    private final GameService gameService;

    public ChannelController(final ChannelService channelService, final UserService userService, final LobbyService lobbyService, final GameService gameService) {
        this.channelService = channelService;
        this.userService = userService;
        this.lobbyService = lobbyService;
        this.gameService = gameService;
    }

    @PostMapping("/lobby/{id}")
    public ResponseEntity<ChannelModel> getOrCreateLobbyChannel(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token) {
        Channel channel = channelService.getOrCreateLobbyChannel(LobbyId.from(id));
        ReferenceTypeModel referenceType = getReferenceType(channel.referenceType(), token);
        return ResponseEntity.ok(ChannelModel.from(channel, referenceType));
    }

    @GetMapping("/lobby/{id}")
    public ResponseEntity<ChannelModel> getLobbyChannel(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token) {
        Channel channel = channelService.getLobbyChannel(LobbyId.from(id));
        ReferenceTypeModel referenceType = getReferenceType(channel.referenceType(), token);
        return ResponseEntity.ok(ChannelModel.from(channel, referenceType));
    }

    @GetMapping("/bot/{id}")
    public ResponseEntity<ChannelModel> getOrCreateBotChannel(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token) {
        Channel channel = channelService.getOrCreateBotChannel(UserId.get(token), GameId.from(id));
        ReferenceTypeModel referenceType = getReferenceType(channel.referenceType(), token);
        return ResponseEntity.ok(ChannelModel.from(channel, referenceType));
    }

    @GetMapping("/friends/{id}")
    public ResponseEntity<ChannelModel> getOrCreatePrivateChannel(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token) {
        Channel channel = channelService.getOrCreatePrivateChannel(UserId.get(token), UserId.from(id));
        ReferenceTypeModel referenceType = getReferenceType(channel.referenceType(), token);
        return ResponseEntity.ok(ChannelModel.from(channel, referenceType));
    }

    private ReferenceTypeModel getReferenceType(final ReferenceType referenceType, final Jwt token) {
        return switch (referenceType) {
            case BotReferenceType botReferenceType ->
                    new BotReferenceTypeModel(getUser(botReferenceType.userId()), getGame(botReferenceType.gameId()));
            case LobbyReferenceType lobbyReferenceType ->
                    new LobbyReferenceTypeModel(lobbyReferenceType.lobbyId().value(), getPlayers(lobbyReferenceType.lobbyId(), token));
            case PrivateReferenceType privateReferenceType ->
                    new PrivateReferenceTypeModel(getUser(privateReferenceType.userId()), getUser(privateReferenceType.otherUserId()));
            default ->
                    throw new ConvertReferenceTypeException();
        };
    }

    private UserModel getUser(final UserId id) {
        ProfileResponse response = userService.getUser(id);
        return new UserModel(response.id(), response.username());
    }

    private GameModel getGame(final GameId id) {
        GameResponse response = gameService.getGame(id);
        return new GameModel(response.id(), response.title(), response.description());
    }

    private List<UserModel> getPlayers(final LobbyId id, final Jwt token) {
        List<PlayerModel> response = lobbyService.findPlayersInLobby(id, token);
        return response.stream().map(player -> new UserModel(player.id(), player.username())).toList();
    }
}