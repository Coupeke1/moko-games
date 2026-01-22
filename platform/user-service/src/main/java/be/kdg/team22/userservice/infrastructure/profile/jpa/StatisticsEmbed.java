package be.kdg.team22.userservice.infrastructure.profile.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class StatisticsEmbed {
    @Column(name = "level", nullable = false)
    private long level;

    @Column(name = "play_time", nullable = false)
    private long playTime;

    protected StatisticsEmbed() {}

    public StatisticsEmbed(final long level, final long playTime) {
        this.level = level;
        this.playTime = playTime;
    }

    public long level() {
        return level;
    }

    public long playTime() {
        return playTime;
    }
}