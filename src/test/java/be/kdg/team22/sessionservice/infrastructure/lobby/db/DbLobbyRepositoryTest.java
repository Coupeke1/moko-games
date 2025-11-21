package be.kdg.team22.sessionservice.infrastructure.lobby.db;

import be.kdg.team22.sessionservice.config.TestcontainersConfig;
import be.kdg.team22.sessionservice.domain.lobby.LobbyStatus;
import be.kdg.team22.sessionservice.domain.lobby.settings.LobbySettings;
import be.kdg.team22.sessionservice.domain.lobby.settings.TicTacToeSettings;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.entities.LobbyEntity;
import be.kdg.team22.sessionservice.infrastructure.lobby.db.entities.LobbyPlayerEmbed;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestcontainersConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DbLobbyRepositoryTest {

    @Autowired
    private LobbyJpaRepository repo;

    @Test
    void saveAndLoadLobby() {
        UUID id = UUID.randomUUID();
        UUID gameId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        LobbySettings settings = new LobbySettings(new TicTacToeSettings(3), 4);

        LobbyEntity entity = new LobbyEntity(
                id,
                gameId,
                ownerId,
                Set.of(new LobbyPlayerEmbed(ownerId, "ownerUser")),
                Set.of(),
                settings,
                LobbyStatus.OPEN,
                Instant.now(),
                Instant.now()
        );

        repo.save(entity);

        Optional<LobbyEntity> loaded = repo.findById(id);
        assertThat(loaded).isPresent();

        LobbyEntity db = loaded.get();

        assertThat(db.getId()).isEqualTo(id);
        assertThat(db.getGameId()).isEqualTo(gameId);
        assertThat(db.getOwnerId()).isEqualTo(ownerId);

        // embedded players
        assertThat(db.getPlayers()).hasSize(1);
        LobbyPlayerEmbed p = db.getPlayers().iterator().next();
        assertThat(p.getId()).isEqualTo(ownerId);
        assertThat(p.getUsername()).isEqualTo("ownerUser");

        // settings mapped correctly
        LobbySettings mapped = db.getSettings();
        assertThat(mapped.maxPlayers()).isEqualTo(4);
        assertThat(mapped.gameSettings()).isInstanceOf(TicTacToeSettings.class);
    }
}
