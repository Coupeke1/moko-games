package be.kdg.team22.userservice.api.profile.models;

import be.kdg.team22.userservice.api.achievement.models.AchievementModel;

import java.util.List;
import java.util.UUID;

public record FilteredProfileModel(UUID id,
                                   String username,
                                   String description,
                                   String image,
                                   StatisticsModel statistics,
                                   List<AchievementModel> achievements,
                                   List<FavouriteGameModel> favourites) {}