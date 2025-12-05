package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.ChatChannelResponse;
import be.kdg.team22.communicationservice.api.chat.models.ChatMessageRequestModel;
import be.kdg.team22.communicationservice.api.chat.models.ChatMessageResponse;
import be.kdg.team22.communicationservice.application.chat.ChatService;
import be.kdg.team22.communicationservice.domain.chat.ChatChannel;
import be.kdg.team22.communicationservice.domain.chat.ChatChannelType;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
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
                userId,
                request.content(),
                token
        );
        return ResponseEntity.ok(ChatMessageResponse.from(message));
    }

    @GetMapping("/{type}/{referenceId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable ChatChannelType type,
            @PathVariable String referenceId,
            @AuthenticationPrincipal Jwt token,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant since
    ) {
        String userId = token.getSubject();

        List<ChatMessage> messages = chatService.getMessages(type, referenceId, since, userId,token);

        return ResponseEntity.ok(
                messages.stream().map(ChatMessageResponse::from).toList()
        );
    }

    @PostMapping("/channel/{type}/{referenceId}")
    public ResponseEntity<ChatChannelResponse> createChannel(
            @PathVariable ChatChannelType type,
            @PathVariable String referenceId
    ) {
        ChatChannel channel = chatService.createChannel(type, referenceId);
        return ResponseEntity.ok(ChatChannelResponse.from(channel));
    }
}