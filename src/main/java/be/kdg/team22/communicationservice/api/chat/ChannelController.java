package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.ChannelModel;
import be.kdg.team22.communicationservice.application.chat.ChannelService;
import be.kdg.team22.communicationservice.application.chat.ChatService;
import be.kdg.team22.communicationservice.domain.chat.channel.Channel;
import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat/channel")
public class ChannelController {
    private final ChatService chatService;
    private final ChannelService channelService;

    public ChannelController(final ChatService chatService, final ChannelService channelService) {
        this.chatService = chatService;
        this.channelService = channelService;
    }

    @PostMapping("/lobby/{id}")
    public ResponseEntity<ChannelModel> createLobbyChannel(@PathVariable UUID id) {
        Channel channel = channelService.getLobbyChannel(LobbyId.from(id));
        return ResponseEntity.ok(ChannelModel.from(channel));
    }
}