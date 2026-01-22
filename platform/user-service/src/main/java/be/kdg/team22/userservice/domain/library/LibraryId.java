package be.kdg.team22.userservice.domain.library;

import be.kdg.team22.userservice.domain.library.exceptions.GameNotFoundException;
import be.kdg.team22.userservice.domain.library.exceptions.LibraryNotFoundException;

import java.util.UUID;

public record LibraryId(UUID value) {
    public static LibraryId create(String value) {
        return new LibraryId(UUID.fromString(value));
    }

    public static LibraryId from(UUID value) {
        return new LibraryId(value);
    }

    public LibraryNotFoundException notFound() {
        throw new LibraryNotFoundException(this);
    }
}