package be.kdg.team22.userservice.api.profile.models;

import be.kdg.team22.userservice.domain.profile.Statistics;

public record StatisticsModel(long level,
                              long playTime) {
    public static StatisticsModel from(final Statistics statistics) {
        return new StatisticsModel(statistics.level(), statistics.playTime());
    }

    public Statistics to() {
        return new Statistics(level, playTime);
    }
}