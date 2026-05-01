package org.test.parking.exception.domain;

public class ConflictException extends DomainException {
    public ConflictException(String message) {
        super(message);
    }
}
