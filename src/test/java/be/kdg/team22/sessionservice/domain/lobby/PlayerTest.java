package be.kdg.team22.sessionservice.domain.lobby;

import be.kdg.team22.sessionservice.domain.player.Player;
import be.kdg.team22.sessionservice.domain.player.PlayerId;
import be.kdg.team22.sessionservice.domain.player.PlayerName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerTest {

    @Test
    void validPlayer_createsSuccessfully() {
        PlayerId playerId = PlayerId.create();
        Player p = new Player(playerId, new PlayerName("mathias"));

        assertThat(p.id()).isEqualTo(playerId);
        assertThat(p.username().toString()).isEqualTo("mathias");
        assertThat(p.ready()).isFalse(); // default = false
    }
}