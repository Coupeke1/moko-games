package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.CreateMessageModel;
import be.kdg.team22.communicationservice.api.chat.models.MessageModel;
import be.kdg.team22.communicationservice.application.chat.ChatService;
import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.message.Message;
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
    private final ChatService service;

    public FriendsChatController(final ChatService service) {
        this.service = service;
    }

    @PostMapping("/{id}")
    public ResponseEntity<MessageModel> sendMessage(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token, @RequestBody final CreateMessageModel request) {
        Message message = service.sendPrivateMessage(UserId.from(id), UserId.get(token), request.content());
        return ResponseEntity.ok(MessageModel.from(message));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<MessageModel>> getMessages(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Instant since) {
        List<Message> messages = service.getPrivateMessages(UserId.get(token), UserId.from(id), since);
        return ResponseEntity.ok(messages.stream().map(MessageModel::from).toList());
    }
}