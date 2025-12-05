package be.kdg.team22.userservice.domain.library.exceptions;

import be.kdg.team22.userservice.domain.library.LibraryId;

public class LibraryNotFoundException extends RuntimeException {
    public LibraryNotFoundException(LibraryId id) {
        super(String.format("Librar with id '%s' ywas not found", id));
    }
}