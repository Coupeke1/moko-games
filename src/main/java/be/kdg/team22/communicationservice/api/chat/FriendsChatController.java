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
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/friends")
public class FriendsChatController {
    private final ChatService chatService;

    public FriendsChatController(final ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/{friendId}")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable UUID friendId,
            @AuthenticationPrincipal Jwt token,
            @RequestBody ChatMessageRequestModel request
    ) {
        ChatMessage msg = chatService.sendMessage(
                ChatChannelType.FRIENDS,
                friendId.toString(),
                token.getSubject(),
                request.content(),
                token
        );
        return ResponseEntity.ok(ChatMessageResponse.from(msg));
    }

    @GetMapping("/{friendId}")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable UUID friendId,
            @AuthenticationPrincipal Jwt token,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant since
    ) {
        List<ChatMessage> messages =
                chatService.getMessages(ChatChannelType.FRIENDS, friendId.toString(), since, token.getSubject(), token);

        return ResponseEntity.ok(messages.stream().map(ChatMessageResponse::from).toList());
    }
}

