package be.kdg.team22.communicationservice.api.chat;

import be.kdg.team22.communicationservice.api.chat.models.BotMessageRequestModel;
import be.kdg.team22.communicationservice.api.chat.models.ChatChannelResponse;
import be.kdg.team22.communicationservice.api.chat.models.ChatMessageResponse;
import be.kdg.team22.communicationservice.application.chat.BotChatService;
import be.kdg.team22.communicationservice.domain.chat.Channel;
import be.kdg.team22.communicationservice.domain.chat.ChatMessage;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chat/bot")
public class BotChatController {
    private final BotChatService botChatService;

    public BotChatController(final BotChatService botChatService) {
        this.botChatService = botChatService;
    }

    @PostMapping
    public ResponseEntity<ChatChannelResponse> createBotChannel(
            @AuthenticationPrincipal final Jwt token
    ) {
        Channel channel = botChatService.createChannel(token.getSubject());
        return ResponseEntity.ok(ChatChannelResponse.from(channel));
    }

    @PostMapping("/{channelId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable final UUID channelId,
            @AuthenticationPrincipal final Jwt token,
            @RequestBody final BotMessageRequestModel request
    ) {
        ChatMessage msg = botChatService.sendMessage(
                channelId,
                token.getSubject(),
                request.content(),
                request.gameName()
        );
        return ResponseEntity.ok(ChatMessageResponse.from(msg));
    }

    @GetMapping("/{channelId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable final UUID channelId,
            @AuthenticationPrincipal final Jwt token,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            final Instant since
    ) {
        List<ChatMessage> messages = botChatService.getMessages(
                channelId,
                token.getSubject(),
                since
        );

        return ResponseEntity.ok(messages.stream().map(ChatMessageResponse::from).toList());
    }

    @PostMapping(value = "/upload-document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestPart("file") MultipartFile file) throws Exception {
        return botChatService.uploadDocument(file.getBytes(), file.getOriginalFilename() != null ? file.getOriginalFilename() : "document.pdf");
    }
}