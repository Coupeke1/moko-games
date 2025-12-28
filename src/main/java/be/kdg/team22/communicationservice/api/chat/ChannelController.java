package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.ChannelModel;
import be.kdg.team22.communicationservice.application.chat.ChannelService;
import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat/channel")
public class ChannelController {
    private final ChannelService service;

    public ChannelController(final ChannelService service) {
        this.service = service;
    }

    @PostMapping("/lobby/{id}")
    public ResponseEntity<ChannelModel> getOrCreateLobbyChannel(@PathVariable final UUID id) {
        Channel channel = service.getOrCreateLobbyChannel(LobbyId.from(id));
        return ResponseEntity.ok(ChannelModel.from(channel));
    }

    @GetMapping("/friends/{id}")
    public ResponseEntity<ChannelModel> getOrCreatePrivateChannel(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token) {
        Channel channel = service.getOrCreatePrivateChannel(UserId.get(token), UserId.from(id));
        return ResponseEntity.ok(ChannelModel.from(channel));
    }
}