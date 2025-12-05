package be.kdg.team22.communicationservice.domain.chat.bot;

import org.springframework.stereotype.Repository;

@Repository
public interface BotChatRepository {
    BotResponse askBot(String model, String userId, String reference, String message);
}
