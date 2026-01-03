package be.kdg.team22.communicationservice.domain.chat.bot;

public interface BotChatRepository {
    BotResponse ask(String question, String gameName);
}