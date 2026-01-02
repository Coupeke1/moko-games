package be.kdg.team22.communicationservice.domain.chat.channel.type;

import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;

public class LobbyReferenceType extends ReferenceType {
    private final LobbyId lobbyId;

    public LobbyReferenceType(final LobbyId lobbyId) {
        super(ChannelType.LOBBY);
        this.lobbyId = lobbyId;
    }

    public LobbyId lobbyId() {
        return lobbyId;
    }
}
