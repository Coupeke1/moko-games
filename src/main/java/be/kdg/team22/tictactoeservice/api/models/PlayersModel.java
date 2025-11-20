package be.kdg.team22.tictactoeservice.api.models;

import be.kdg.team22.tictactoeservice.domain.player.PlayerRole;

import java.util.Map;
import java.util.UUID;

public record PlayersModel(Map<UUID, PlayerRole> players) {
}
