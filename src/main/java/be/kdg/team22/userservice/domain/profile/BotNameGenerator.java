package be.kdg.team22.userservice.domain.profile;

import java.util.List;
import java.util.Random;

public final class BotNameGenerator {
    private static final List<String> BASE_NAMES = List.of(
            "moko", "mojo", "joto", "jojo", "zoko", "robo", "doko", "lomo", "koko", "momo"
    );
    private static final Random RANDOM = new Random();

    private BotNameGenerator() {
    }

    public static ProfileName randomName() {

        return new ProfileName(BASE_NAMES.get(RANDOM.nextInt(BASE_NAMES.size())));
    }

    public static ProfileEmail randomEmail(ProfileName name) {
        return new ProfileEmail(name.value() + "@bot.moko.local");
    }
}
