package be.kdg.team22.sessionservice.infrastructure.lobby.jpa;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PlayerEmbedTest {

    @Test
    void constructor_setsFieldsCorrectly() {
        UUID id = UUID.randomUUID();
        String username = "mathias";
        boolean ready = true;

        PlayerEmbed embed = new PlayerEmbed(id, username, "", ready);

        assertThat(embed.id()).isEqualTo(id);
        assertThat(embed.username()).isEqualTo(username);
        assertThat(embed.ready()).isTrue();
    }

    @Test
    void constructor_allowsFalseReady() {
        UUID id = UUID.randomUUID();
        PlayerEmbed embed = new PlayerEmbed(id, "piet", "", false);

        assertThat(embed.ready()).isFalse();
    }

    @Test
    void noArgsConstructor_isAccessibleViaReflection() throws Exception {
        Constructor<PlayerEmbed> ctor = PlayerEmbed.class.getDeclaredConstructor();
        ctor.setAccessible(true);

        PlayerEmbed embed = ctor.newInstance();

        assertThat(embed).isNotNull();
    }
}