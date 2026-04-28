package org.test.parking.exception.domain;

public class SlotNotFoundException extends DomainException {
    public SlotNotFoundException(String message) {
        super(message);
    }
}
