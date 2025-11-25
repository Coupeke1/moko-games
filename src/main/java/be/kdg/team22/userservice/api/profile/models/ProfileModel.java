package be.kdg.team22.userservice.api.profile.models;

import be.kdg.team22.userservice.domain.profile.Profile;

import java.time.Instant;
import java.util.UUID;

public record ProfileModel(UUID id,
                           String username,
                           String email,
                           String description,
                           String image,
                           StatisticsModel statistics,
                           ModulesModel modules,
                           Instant createdAt) {
    public static ProfileModel from(Profile profile) {
        return new ProfileModel(profile.id().value(), profile.username().value(), profile.email().value(), profile.description(), profile.image(), StatisticsModel.from(profile.statistics()), ModulesModel.from(profile.modules()), profile.createdAt());
    }
}