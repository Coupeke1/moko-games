package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.ChatChannelResponse;
import be.kdg.team22.communicationservice.application.chat.ChatService;
import be.kdg.team22.communicationservice.domain.chat.Channel;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat/channel")
public class ChannelController {
    private final ChatService chatService;

    public ChannelController(final ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/lobby/{lobbyId}")
    public ResponseEntity<ChatChannelResponse> createLobbyChannel(@PathVariable String lobbyId) {
        Channel channel = chatService.createChannel(ChatChannelType.LOBBY, lobbyId);
        return ResponseEntity.ok(ChatChannelResponse.from(channel));
    }

    @PostMapping("/bot/{referenceId}")
    public ResponseEntity<ChatChannelResponse> createAIChannel(@PathVariable String referenceId) {
        Channel channel = chatService.createChannel(ChatChannelType.BOT, referenceId);
        return ResponseEntity.ok(ChatChannelResponse.from(channel));
    }
}