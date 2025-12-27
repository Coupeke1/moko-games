package be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.type;

import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.BotReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.LobbyReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.PrivateReferenceType;
import be.kdg.team22.communicationservice.domain.chat.channel.type.ReferenceType;
import be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.type.exceptions.ConvertReferenceTypeException;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "reference_types")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "ref_type")
public abstract class ReferenceTypeEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChannelType type;

    protected ReferenceTypeEntity() {}

    protected ReferenceTypeEntity(final ChannelType type) {
        this.type = type;
    }

    public static ReferenceTypeEntity from(final ReferenceType referenceType) {
        switch (referenceType.type()) {
            case BOT -> {
                BotReferenceType botReferenceType = (BotReferenceType) referenceType;
                return new BotReferenceTypeEntity(botReferenceType.userId().value());
            }
            case LOBBY -> {
                LobbyReferenceType lobbyReferenceType = (LobbyReferenceType) referenceType;
                return new LobbyReferenceTypeEntity(lobbyReferenceType.lobbyId().value());
            }
            case FRIENDS -> {
                PrivateReferenceType privateReferenceType = (PrivateReferenceType) referenceType;
                return new PrivateReferenceTypeEntity(privateReferenceType.userId().value(), privateReferenceType.otherUserId().value());
            }
        }

        throw new ConvertReferenceTypeException();
    }

    public abstract ReferenceType to();

    public ChannelType type() {
        return type;
    }
}
