package org.test.parking.exception.domain;

public class RestrictedSlotOperationException extends DomainException {
    public RestrictedSlotOperationException(String message) {
        super(message);
    }
}
