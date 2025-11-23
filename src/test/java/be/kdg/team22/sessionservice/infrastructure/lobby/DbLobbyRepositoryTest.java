package be.kdg.team22.sessionservice.infrastructure.lobby;

import be.kdg.team22.sessionservice.config.TestcontainersConfig;
import be.kdg.team22.sessionservice.domain.lobby.GameId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyId;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.LobbyEntity;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.LobbyJpaRepository;
import be.kdg.team22.sessionservice.infrastructure.lobby.jpa.PlayerEmbed;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbLobbyRepositoryTest {

    @Autowired
    private LobbyJpaRepository repo;

    @Test
    void saveAndLoadLobby() {
        LobbyId id = LobbyId.create();
        GameId gameId = GameId.create();
        PlayerId ownerId = PlayerId.create();

        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        LobbyEntity entity = new LobbyEntity(
                id.value(),
                gameId.value(),
                ownerId.value(),
                Set.of(new PlayerEmbed(ownerId.value(), "ownerUser")),
                Set.of(),
                settings,
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now()
        );

        repo.save(entity);

        Optional<LobbyEntity> loaded = repo.findById(id.value());
        assertThat(loaded).isPresent();

        LobbyEntity db = loaded.get();

        assertThat(db.id()).isEqualTo(id);
        assertThat(db.gameId()).isEqualTo(gameId);
        assertThat(db.ownerId()).isEqualTo(ownerId);

        assertThat(db.players()).hasSize(1);
        PlayerEmbed p = db.players().iterator().next();
        assertThat(p.id()).isEqualTo(ownerId);
        assertThat(p.username()).isEqualTo("ownerUser");

        LobbySettings mapped = db.settings();
        assertThat(mapped.maxPlayers()).isEqualTo(4);
        assertThat(mapped.gameSettings()).isInstanceOf(TicTacToeSettings.class);
    }
}
