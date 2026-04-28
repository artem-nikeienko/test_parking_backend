package org.test.parking.exception.domain;

public class LevelNotFoundException extends DomainException {
    public LevelNotFoundException(String message) {
        super(message);
    }
}
