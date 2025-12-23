package be.kdg.team22.socialservice.api.friends.models;

import be.kdg.team22.socialservice.infrastructure.user.StatisticsResponse;

public record StatisticsModel(long level,
                              long playTime) {
    public static StatisticsModel from(final StatisticsResponse statistics) {
        return new StatisticsModel(statistics.level(), statistics.playTime());
    }
}