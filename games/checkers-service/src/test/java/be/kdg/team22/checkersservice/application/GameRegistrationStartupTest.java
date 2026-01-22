package be.kdg.team22.checkersservice.application;

import be.kdg.team22.checkersservice.config.GameInfoProperties;
import be.kdg.team22.checkersservice.domain.register.exceptions.GameNotRegisteredException;
import be.kdg.team22.checkersservice.infrastructure.register.ExternalRegisterRepository;
import be.kdg.team22.checkersservice.infrastructure.register.RegisterGameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameRegistrationStartupTest {

    @Mock
    private ExternalRegisterRepository externalRegisterRepository;

    @Mock
    private GameInfoProperties gameInfo;

    private GameRegistrationStartup gameRegistrationStartup;

    @BeforeEach
    void setup() {
        gameRegistrationStartup = new GameRegistrationStartup(externalRegisterRepository, gameInfo);
        setupGameInfoProperties();
    }

    private void setupGameInfoProperties() {
        when(gameInfo.name()).thenReturn("checkers");
        when(gameInfo.backendUrl()).thenReturn("http://localhost:8086");
        when(gameInfo.frontendUrl()).thenReturn("http://localhost:5174");
        when(gameInfo.startEndpoint()).thenReturn("/api/games");
        when(gameInfo.healthEndpoint()).thenReturn("/actuator/health");
        when(gameInfo.title()).thenReturn("Checkers");
        when(gameInfo.description()).thenReturn("A classic game");
        when(gameInfo.image()).thenReturn("http://image.url");
        when(gameInfo.price()).thenReturn(BigDecimal.valueOf(25));
        when(gameInfo.category()).thenReturn("PARTY");
    }

    @Test
    void shouldRegisterGameWhenGameDoesNotExist() {
        when(externalRegisterRepository.registerGame(any())).thenReturn(true);

        gameRegistrationStartup.registerGameOnStartup();

        ArgumentCaptor<RegisterGameRequest> captor = ArgumentCaptor.forClass(RegisterGameRequest.class);
        verify(externalRegisterRepository).registerGame(captor.capture());

        RegisterGameRequest request = captor.getValue();
        assertEquals("checkers", request.name());
        assertEquals("http://localhost:8086", request.backendUrl());
        assertEquals("http://localhost:5174", request.frontendUrl());
    }

    @Test
    void shouldRegisterGameSuccessfully() {
        when(externalRegisterRepository.registerGame(any())).thenReturn(true);

        gameRegistrationStartup.registerGameOnStartup();

        ArgumentCaptor<RegisterGameRequest> captor = ArgumentCaptor.forClass(RegisterGameRequest.class);
        verify(externalRegisterRepository).registerGame(captor.capture());

        RegisterGameRequest request = captor.getValue();
        assertEquals("checkers", request.name());
    }

    @Test
    void shouldThrowGameNotRegisteredExceptionWhenRegistrationFails() {
        when(externalRegisterRepository.registerGame(any())).thenReturn(false);

        assertThrows(GameNotRegisteredException.class, 
            () -> gameRegistrationStartup.registerGameOnStartup());
    }

    @Test
    void shouldPropagateGameServiceNotReachableException() {
        when(externalRegisterRepository.registerGame(any()))
                .thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(RuntimeException.class,
            () -> gameRegistrationStartup.registerGameOnStartup());
    }
}
