package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.channel.ChannelModel;
import be.kdg.team22.communicationservice.api.chat.models.channel.type.*;
import be.kdg.team22.communicationservice.api.chat.models.channel.type.exceptions.ConvertReferenceTypeException;
import be.kdg.team22.communicationservice.application.chat.ChannelService;
import be.kdg.team22.communicationservice.application.chat.UserService;
import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.GameId;
import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.LobbyReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.ReferenceType;
import be.kdg.team22.communicationservice.infrastructure.user.ProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat/channel")
public class ChannelController {
    private final ChannelService channelService;
    private final UserService userService;

    public ChannelController(final ChannelService channelService, final UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
    }

    @PostMapping("/lobby/{id}")
    public ResponseEntity<ChannelModel> getOrCreateLobbyChannel(@PathVariable final UUID id) {
        Channel channel = channelService.getOrCreateLobbyChannel(LobbyId.from(id));
        ReferenceTypeModel referenceType = getReferenceType(channel.referenceType());
        return ResponseEntity.ok(ChannelModel.from(channel, referenceType));
    }

    @GetMapping("/bot/{id}")
    public ResponseEntity<ChannelModel> getOrCreateBotChannel(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token) {
        Channel channel = channelService.getOrCreateBotChannel(UserId.get(token), GameId.from(id));
        ReferenceTypeModel referenceType = getReferenceType(channel.referenceType());
        return ResponseEntity.ok(ChannelModel.from(channel, referenceType));
    }

    @GetMapping("/friends/{id}")
    public ResponseEntity<ChannelModel> getOrCreatePrivateChannel(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token) {
        Channel channel = channelService.getOrCreatePrivateChannel(UserId.get(token), UserId.from(id));
        ReferenceTypeModel referenceType = getReferenceType(channel.referenceType());
        return ResponseEntity.ok(ChannelModel.from(channel, referenceType));
    }

    private ReferenceTypeModel getReferenceType(final ReferenceType referenceType) {
        return switch (referenceType) {
            case BotReferenceType botReferenceType ->
                    new BotReferenceTypeModel(botReferenceType.userId().value(), botReferenceType.botId().value(), botReferenceType.gameId().value());
            case LobbyReferenceType lobbyReferenceType ->
                    new LobbyReferenceTypeModel(lobbyReferenceType.lobbyId().value());
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
}