package be.kdg.team22.userservice.api.profile.models;

import be.kdg.team22.userservice.domain.profile.Modules;

public record ModulesModel(boolean achievements,
                           boolean favourites) {
    public static ModulesModel from(final Modules modules) {
        return new ModulesModel(modules.achievements(), modules.favourites());
    }

    public Modules to() {
        return new Modules(achievements, favourites);
    }
}