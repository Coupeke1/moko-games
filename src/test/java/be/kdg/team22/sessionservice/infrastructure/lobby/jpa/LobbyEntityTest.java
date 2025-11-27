package be.kdg.team22.sessionservice.infrastructure.lobby.jpa;

import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LobbyEntityTest {

    private Player p(PlayerId id, String name, boolean ready) {
        return new Player(id, new PlayerName(name), "", ready);
    }

    @Test
    void fromDomain_mapsAllFields() {
        LobbyId id = LobbyId.create();
        GameId gameId = GameId.create();
        PlayerId ownerId = PlayerId.create();

        Player owner = p(ownerId, "owner", true);
        Player p1 = p(PlayerId.create(), "jan", false);

        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        Lobby domain = new Lobby(
                id,
                gameId,
                ownerId,
                List.of(owner, p1),
                Set.of(PlayerId.from(UUID.randomUUID())),
                settings,
                LobbyStatus.OPEN,
                Instant.parse("2024-01-01T00:00:00Z"),
                Instant.parse("2024-01-02T00:00:00Z"),
                GameId.from(UUID.fromString("00000000-0000-0000-0000-000000000999"))
        );

        LobbyEntity entity = LobbyEntity.fromDomain(domain);

        assertThat(entity.id()).isEqualTo(id.value());
        assertThat(entity.gameId()).isEqualTo(gameId.value());
        assertThat(entity.ownerId()).isEqualTo(ownerId.value());
        assertThat(entity.status()).isEqualTo(LobbyStatus.OPEN);
        assertThat(entity.settings().maxPlayers()).isEqualTo(4);
        assertThat(entity.settings().gameSettings()).isInstanceOf(TicTacToeSettings.class);

        assertThat(entity.players()).hasSize(2);
        assertThat(entity.players().stream().anyMatch(e -> e.username().equals("owner") && e.ready())).isTrue();
        assertThat(entity.players().stream().anyMatch(e -> e.username().equals("jan") && !e.ready())).isTrue();

        assertThat(entity.startedGameId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000999"));
    }

    @Test
    void toDomain_mapsBackCorrectly() {
        UUID id = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        PlayerEmbed e1 = new PlayerEmbed(ownerId, "owner", "", true);
        PlayerEmbed e2 = new PlayerEmbed(UUID.randomUUID(), "other", "", false);

        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 5);

        UUID startedGame = UUID.randomUUID();

        LobbyEntity entity = new LobbyEntity(
                id,
                gameId,
                ownerId,
                Set.of(e1, e2),
                Set.of(UUID.randomUUID()),
                settings,
                LobbyStatus.CLOSED,
                Instant.parse("2024-05-05T10:00:00Z"),
                Instant.parse("2024-05-06T10:00:00Z"),
                startedGame
        );

        Lobby domain = entity.toDomain();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.gameId().value()).isEqualTo(gameId);
        assertThat(domain.ownerId().value()).isEqualTo(ownerId);
        assertThat(domain.status()).isEqualTo(LobbyStatus.CLOSED);
        assertThat(domain.settings().maxPlayers()).isEqualTo(5);

        assertThat(domain.players()).hasSize(2);
        assertThat(domain.players().stream().anyMatch(p -> p.username().value().equals("owner") && p.ready())).isTrue();
        assertThat(domain.players().stream().anyMatch(p -> p.username().value().equals("other") && !p.ready())).isTrue();

        assertThat(domain.startedGameId().orElseThrow().value()).isEqualTo(startedGame);
    }

    @Test
    void toDomain_handlesNullStartedGameId() {
        UUID id = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        LobbyEntity entity = new LobbyEntity(
                id,
                gameId,
                ownerId,
                Set.of(new PlayerEmbed(ownerId, "owner", "", false)),
                Set.of(),
                settings,
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now(),
                null
        );

        Lobby domain = entity.toDomain();

        assertThat(domain.startedGameId()).isEmpty();
    }
}