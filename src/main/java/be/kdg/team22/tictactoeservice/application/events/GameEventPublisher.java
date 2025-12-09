package be.kdg.team22.tictactoeservice.application.events;

import be.kdg.team22.tictactoeservice.domain.events.GameDrawEvent;
import be.kdg.team22.tictactoeservice.domain.events.GameEndedEvent;
import be.kdg.team22.tictactoeservice.domain.events.GameWonEvent;

public interface GameEventPublisher {
    void publishGameWon(GameWonEvent event);

    void publishGameDraw(GameDrawEvent event);

    void publishGameEnded(GameEndedEvent event);
}