package be.kdg.team22.sessionservice.infrastructure.lobby;

import be.kdg.team22.sessionservice.config.TestcontainersConfig;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.BotEmbed;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.LobbyEntity;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.LobbyJpaRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.PlayerEmbed;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbLobbyRepositoryTest {

    @Autowired
    private LobbyJpaRepository repo;

    @Test
    void saveAndLoadLobby_withBot() {
        UUID id = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        PlayerEmbed owner = new PlayerEmbed(ownerId, "ownerUser", "img.png", true);

        UUID invitedId = UUID.randomUUID();

        UUID botId = UUID.randomUUID();
        BotEmbed bot = new BotEmbed(botId, "BOT-MOKO", "bot.png", true);

        UUID startedGameId = UUID.randomUUID();

        LobbyEntity entity = new LobbyEntity(id, gameId, ownerId, List.of(owner), List.of(invitedId), settings, LobbyStatus.OPEN, Instant.now(), Instant.now(), startedGameId, bot);

        repo.save(entity);
        Optional<LobbyEntity> loaded = repo.findById(id);

        assertThat(loaded).isPresent();
        LobbyEntity db = loaded.get();

        assertThat(db.id()).isEqualTo(id);
        assertThat(db.gameId()).isEqualTo(gameId);
        assertThat(db.ownerId()).isEqualTo(ownerId);

        assertThat(db.players()).hasSize(1);
        PlayerEmbed p = db.players().iterator().next();
        assertThat(p.id()).isEqualTo(ownerId);
        assertThat(p.username()).isEqualTo("ownerUser");
        assertThat(p.image()).isEqualTo("img.png");
        assertThat(p.ready()).isTrue();

        assertThat(db.invitedPlayerIds()).contains(invitedId);

        assertThat(db.bot()).isNotNull();
        assertThat(db.bot().id()).isEqualTo(botId);
        assertThat(db.bot().username()).isEqualTo("BOT-MOKO");
        assertThat(db.bot().image()).isEqualTo("bot.png");
        assertThat(db.bot().ready()).isTrue();
        LobbySettings mapped = db.settings();
        assertThat(mapped.maxPlayers()).isEqualTo(4);
        assertThat(mapped.gameSettings()).isInstanceOf(TicTacToeSettings.class);

        assertThat(db.startedGameId()).isEqualTo(startedGameId);
    }
}