package be.kdg.team22.communicationservice.infrastructure.chat.jpa.channel.type.exceptions;

public class ConvertReferenceTypeException extends RuntimeException {
    public ConvertReferenceTypeException() {
        super("Could not convert reference type");
    }
}
