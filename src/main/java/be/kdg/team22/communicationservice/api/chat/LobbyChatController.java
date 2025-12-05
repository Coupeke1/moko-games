package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.ChatMessageRequestModel;
import be.kdg.team22.communicationservice.api.chat.models.ChatMessageResponse;
import be.kdg.team22.communicationservice.application.chat.ChatService;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/chat/lobby")
public class LobbyChatController {
    private final ChatService chatService;

    public LobbyChatController(final ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/{lobbyId}")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable String lobbyId,
            @AuthenticationPrincipal Jwt token,
            @RequestBody ChatMessageRequestModel request
    ) {
        ChatMessage msg = chatService.sendMessage(
                ChatChannelType.LOBBY,
                lobbyId,
                token.getSubject(),
                request.content(),
                token
        );
        return ResponseEntity.ok(ChatMessageResponse.from(msg));
    }

    @GetMapping("/{lobbyId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable String lobbyId,
            @AuthenticationPrincipal Jwt token,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant since
    ) {
        List<ChatMessage> messages =
                chatService.getMessages(ChatChannelType.LOBBY, lobbyId, since, token.getSubject(), token);

        return ResponseEntity.ok(messages.stream().map(ChatMessageResponse::from).toList());
    }
}
