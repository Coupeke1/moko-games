package be.kdg.team22.communicationservice.domain.chat.channel.type;

import be.kdg.team22.communicationservice.domain.chat.channel.ChannelType;

public abstract class ReferenceType {
    final ChannelType type;

    protected ReferenceType(ChannelType type) {
        this.type = type;
    }

    public ChannelType type() {
        return type;
    }
}
