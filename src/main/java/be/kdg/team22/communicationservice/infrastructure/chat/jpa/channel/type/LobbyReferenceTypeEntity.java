package be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.type;

import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.LobbyId;
import be.kdg.team22.communicationservice.domain.chat.channel.type.LobbyReferenceType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.util.UUID;

@Entity
@DiscriminatorValue("LOBBY")
public class LobbyReferenceTypeEntity extends ReferenceTypeEntity {
    @Column
    private UUID lobbyId;

    protected LobbyReferenceTypeEntity() {}

    public LobbyReferenceTypeEntity(final UUID lobbyId) {
        super(ChannelType.LOBBY);
        this.lobbyId = lobbyId;
    }

    public LobbyReferenceType to() {
        return new LobbyReferenceType(LobbyId.from(lobbyId));
    }

    public UUID lobbyId() {
        return lobbyId;
    }
}
