package be.kdg.team22.userservice.api.profile.models;

import be.kdg.team22.userservice.domain.profile.Modules;

public record EditModulesModel(boolean achievements,
                               boolean favourites) {
    public static EditModulesModel from(final Modules modules) {
        return new EditModulesModel(modules.achievements(), modules.favourites());
    }

    public Modules to() {
        return new Modules(achievements, favourites);
    }
}