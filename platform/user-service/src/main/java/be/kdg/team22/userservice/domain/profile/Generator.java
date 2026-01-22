package be.kdg.team22.userservice.domain.profile;

import java.util.List;
import java.util.Random;

public final class Generator {
    private static final List<String> BASE_NAMES = List.of("moko", "mojo", "joto", "jojo", "zoko", "robo", "doko", "lomo", "koko", "momo");
    private static final Random RANDOM = new Random();

    public static ProfileName name() {
        return new ProfileName(BASE_NAMES.get(RANDOM.nextInt(BASE_NAMES.size())));
    }

    public static ProfileEmail email(final ProfileName name) {
        return new ProfileEmail(name.value() + "@moko.games");
    }
}
