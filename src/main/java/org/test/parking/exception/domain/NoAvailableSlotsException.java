package org.test.parking.exception.domain;

public class NoAvailableSlotsException extends DomainException {
    public NoAvailableSlotsException(String message) {
        super(message);
    }
}
