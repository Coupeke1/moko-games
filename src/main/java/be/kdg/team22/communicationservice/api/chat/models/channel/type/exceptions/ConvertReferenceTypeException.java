package be.kdg.team22.communicationservice.api.chat.models.channel.type.exceptions;

public class ConvertReferenceTypeException extends RuntimeException {
    public ConvertReferenceTypeException() {
        super("Could not convert reference type");
    }
}
