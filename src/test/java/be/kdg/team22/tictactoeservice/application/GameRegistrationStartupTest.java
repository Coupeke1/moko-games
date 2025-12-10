package be.kdg.team22.tictactoeservice.application;

import be.kdg.team22.tictactoeservice.config.GameInfoProperties;
import be.kdg.team22.tictactoeservice.domain.register.exceptions.GameNotRegisteredException;
import be.kdg.team22.tictactoeservice.domain.register.GameRegisterId;
import be.kdg.team22.tictactoeservice.infrastructure.register.ExternalRegisterRepository;
import be.kdg.team22.tictactoeservice.infrastructure.register.GameResponse;
import be.kdg.team22.tictactoeservice.infrastructure.register.RegisterGameRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        when(gameInfo.name()).thenReturn("tic-tac-toe");
        when(gameInfo.backendUrl()).thenReturn("http://localhost:8086");
        when(gameInfo.frontendUrl()).thenReturn("http://localhost:5174");
        when(gameInfo.startEndpoint()).thenReturn("/api/games");
        when(gameInfo.healthEndpoint()).thenReturn("/actuator/health");
        when(gameInfo.title()).thenReturn("Tic Tac Toe");
        when(gameInfo.description()).thenReturn("A classic game");
        when(gameInfo.image()).thenReturn("http://image.url");
        when(gameInfo.price()).thenReturn(BigDecimal.valueOf(25));
        when(gameInfo.category()).thenReturn("PARTY");
    }

    @Test
    void shouldRegisterGameWhenGameDoesNotExist() {
        when(externalRegisterRepository.getGame("tic-tac-toe")).thenReturn(null);
        when(externalRegisterRepository.registerGame(any())).thenReturn(true);

        gameRegistrationStartup.registerGameOnStartup();

        ArgumentCaptor<RegisterGameRequest> captor = ArgumentCaptor.forClass(RegisterGameRequest.class);
        verify(externalRegisterRepository).registerGame(captor.capture());
        verify(externalRegisterRepository, never()).updateGame(any(), any());

        RegisterGameRequest request = captor.getValue();
        assertEquals("tic-tac-toe", request.name());
        assertEquals("http://localhost:8086", request.backendUrl());
        assertEquals("http://localhost:5174", request.frontendUrl());
    }

    @Test
    void shouldUpdateGameWhenGameAlreadyExists() {
        UUID gameId = UUID.randomUUID();
        GameResponse existingGame = new GameResponse(
                gameId,
                "tic-tac-toe",
                "Tic Tac Toe",
                "A classic game",
                "http://image.url",
                "http://localhost:5174",
                "/api/games",
                true,
                null,
                null,
                null
        );
        
        when(externalRegisterRepository.getGame("tic-tac-toe")).thenReturn(existingGame);

        gameRegistrationStartup.registerGameOnStartup();

        ArgumentCaptor<RegisterGameRequest> captor = ArgumentCaptor.forClass(RegisterGameRequest.class);
        verify(externalRegisterRepository).updateGame(
                eq(GameRegisterId.fromUuid(gameId)),
                captor.capture()
        );
        verify(externalRegisterRepository, never()).registerGame(any());

        RegisterGameRequest request = captor.getValue();
        assertEquals("tic-tac-toe", request.name());
    }

    @Test
    void shouldThrowGameNotRegisteredExceptionWhenRegistrationFails() {
        when(externalRegisterRepository.getGame("tic-tac-toe")).thenReturn(null);
        when(externalRegisterRepository.registerGame(any())).thenReturn(false);

        assertThrows(GameNotRegisteredException.class, 
            () -> gameRegistrationStartup.registerGameOnStartup());
    }

    @Test
    void shouldPropagateGameServiceNotReachableException() {
        when(externalRegisterRepository.getGame("tic-tac-toe"))
                .thenThrow(new RuntimeException("Service unavailable"));

        assertThrows(RuntimeException.class,
            () -> gameRegistrationStartup.registerGameOnStartup());

        verify(externalRegisterRepository, never()).registerGame(any());
        verify(externalRegisterRepository, never()).updateGame(any(), any());
    }
}
