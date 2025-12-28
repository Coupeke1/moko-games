package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.CreateMessageModel;
import be.kdg.team22.communicationservice.api.chat.models.MessageModel;
import be.kdg.team22.communicationservice.application.chat.ChatService;
import be.kdg.team22.communicationservice.domain.chat.UserId;
import be.kdg.team22.communicationservice.domain.chat.channel.ChannelId;
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

    @GetMapping("/{id}")
    public ResponseEntity<List<MessageModel>> getMessages(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Instant since) {
        List<Message> messages = service.getMessages(ChannelId.from(id), UserId.get(token), since, token);
        return ResponseEntity.ok(messages.stream().map(MessageModel::from).toList());
    }

    @PostMapping("/{id}")
    public ResponseEntity<MessageModel> sendMessage(@PathVariable final UUID id, @AuthenticationPrincipal final Jwt token, @RequestBody final CreateMessageModel request) {
        Message message = service.sendPrivateMessage(new ChannelId(id), UserId.get(token), request.content(), token);
        return ResponseEntity.ok(MessageModel.from(message));
    }
}