package be.kdg.team22.communicationservice.domain.chat.bot;

public interface BotChatRepository {
    BotResponse askBot(String question, String gameName);
}