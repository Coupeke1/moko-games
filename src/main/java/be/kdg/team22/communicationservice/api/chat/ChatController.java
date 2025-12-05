package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.ChatMessageRequestModel;
import be.kdg.team22.communicationservice.api.chat.models.ChatMessageResponse;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import be.kdg.team22.communicationservice.domain.chat.ChatService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import static org.springframework.format.annotation.DateTimeFormat.ISO;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/{type}/{referenceId}")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable ChatChannelType type,
            @PathVariable String referenceId,
            @AuthenticationPrincipal Jwt token,
            @RequestBody ChatMessageRequestModel request
    ) {
        String userId = token.getSubject();

        ChatMessage message = chatService.sendMessage(
                type,
                referenceId,
                "user:" + userId,
                request.content()
        );

        return ResponseEntity.ok(ChatMessageResponse.fromDomain(message));
    }

    @GetMapping("/{type}/{referenceId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable ChatChannelType type,
            @PathVariable String referenceId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = ISO.DATE_TIME)
            Instant since
    ) {
        List<ChatMessage> messages = chatService.getMessages(type, referenceId, since);

        List<ChatMessageResponse> dtos = messages.stream()
                .map(ChatMessageResponse::fromDomain)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}