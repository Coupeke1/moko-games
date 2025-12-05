package be.kdg.team22.communicationservice.domain.chat.ai;

import org.springframework.stereotype.Repository;

@Repository
public interface AiChatRepository {
    AIResponse askAI(String model, String userId, String reference, String message);
}
