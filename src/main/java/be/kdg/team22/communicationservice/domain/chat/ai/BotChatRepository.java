package be.kdg.team22.communicationservice.domain.chat.ai;

import org.springframework.stereotype.Repository;

@Repository
public interface BotChatRepository {
    AIResponse askBot(String model, String userId, String reference, String message);
}
