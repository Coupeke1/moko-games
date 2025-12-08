package be.kdg.team22.checkersservice.application.events;

import be.kdg.team22.checkersservice.domain.events.*;

public interface GameEventPublisher {
    void publishGameDraw(GameDrawEvent event);
    void publishGameLost(GameLostEvent event);
    void publishGameWon(GameWonEvent event);
    void publishKingPromotionEvent(KingPromotionEvent event);
    void publishMultiCaptureEvent(MultiCaptureEvent event);
    void publishThreeKingsEvent(ThreeKingsEvent event);
}
