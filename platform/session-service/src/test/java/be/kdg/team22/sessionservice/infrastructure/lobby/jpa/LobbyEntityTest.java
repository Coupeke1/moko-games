package be.kdg.team22.sessionservice.infrastructure.lobby.jpa;

import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.Lobby;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LobbyEntityTest {

    private final LobbySettings settings = new LobbySettings(
            2,
            Map.of("boardSize", 3)
    );

    private Player p(PlayerId id, String name, String img, boolean ready) {
        return new Player(id, new PlayerName(name), img, ready);
    }

    @Test
    void from_mapsAllFields_includingAi() {
        LobbyId id = LobbyId.create();
        GameId gameId = GameId.create();
        PlayerId ownerId = PlayerId.create();

        Player owner = p(ownerId, "owner", "img1.png", true);

        Player bot = Player.bot(PlayerId.create(), new PlayerName("BOT-MOKO"), "bot.png");


        Lobby domain = new Lobby(id, gameId, ownerId, List.of(owner), Set.of(PlayerId.from(UUID.randomUUID())), settings, LobbyStatus.OPEN, Instant.parse("2024-01-01T00:00:00Z"), Instant.parse("2024-01-02T00:00:00Z"), GameId.from(UUID.fromString("00000000-0000-0000-0000-000000000999")));

        domain.addBot(ownerId, bot);

        LobbyEntity entity = LobbyEntity.from(domain);

        assertThat(entity.id()).isEqualTo(id.value());
        assertThat(entity.gameId()).isEqualTo(gameId.value());
        assertThat(entity.ownerId()).isEqualTo(ownerId.value());
        assertThat(entity.status()).isEqualTo(LobbyStatus.OPEN);

        assertThat(entity.players()).hasSize(1);
        assertThat(entity.players().stream().anyMatch(e -> e.username().equals("owner"))).isTrue();

        assertThat(entity.bot()).isNotNull();
        assertThat(entity.bot().username()).isEqualTo("BOT-MOKO");
        assertThat(entity.bot().image()).isEqualTo("bot.png");

        assertThat(entity.startedGameId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000999"));
    }

    @Test
    void to_mapsBackCorrectly_includingAi() {
        UUID id = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        PlayerEmbed e1 = new PlayerEmbed(ownerId, "owner", "img.png", true);

        UUID invited = UUID.randomUUID();

        BotEmbed bot = new BotEmbed(UUID.randomUUID(), "BOT-XYZ", "bot.png", true);

        UUID startedGame = UUID.randomUUID();

        LobbyEntity entity = new LobbyEntity(id, gameId, ownerId, List.of(e1), List.of(invited), settings, LobbyStatus.CLOSED, Instant.parse("2024-05-05T10:00:00Z"), Instant.parse("2024-05-06T10:00:00Z"), startedGame, bot);

        Lobby domain = entity.to();

        assertThat(domain.id().value()).isEqualTo(id);
        assertThat(domain.gameId().value()).isEqualTo(gameId);
        assertThat(domain.ownerId().value()).isEqualTo(ownerId);
        assertThat(domain.status()).isEqualTo(LobbyStatus.CLOSED);

        assertThat(domain.players()).hasSize(1);

        assertThat(domain.players().stream().anyMatch(p -> p.username().value().equals("owner") && p.ready())).isTrue();

        assertThat(domain.invitedPlayers()).hasSize(1);
        assertThat(domain.invitedPlayers().iterator().next().value()).isEqualTo(invited);

        assertThat(domain.hasBot()).isTrue();
        assertThat(domain.bot().username().value()).isEqualTo("BOT-XYZ");
        assertThat(domain.bot().image()).isEqualTo("bot.png");
        assertThat(domain.bot().isBot()).isTrue();

        assertThat(domain.startedGameId().orElseThrow().value()).isEqualTo(startedGame);
    }

    @Test
    void to_handlesNullStartedGameId() {
        UUID id = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();


        LobbyEntity entity = new LobbyEntity(id, gameId, ownerId, List.of(new PlayerEmbed(ownerId, "owner", "", false)), List.of(), settings, LobbyStatus.OPEN, Instant.now(), Instant.now(), null, null);

        Lobby domain = entity.to();
        assertThat(domain.startedGameId()).isEmpty();
        assertThat(domain.hasBot()).isFalse();
    }

    @Test
    void to_noAi_doesNotModifyPlayers() {
        UUID id = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        PlayerEmbed owner = new PlayerEmbed(ownerId, "owner", "", true);

        LobbyEntity entity = new LobbyEntity(id, gameId, ownerId, List.of(owner), List.of(), settings, LobbyStatus.OPEN, Instant.now(), Instant.now(), null, null);

        Lobby domain = entity.to();

        assertThat(domain.players()).hasSize(1);
        assertThat(domain.hasBot()).isFalse();
    }
}