package org.test.parking.exception.domain;

public class RestrictedLotOperationException extends DomainException {
    public RestrictedLotOperationException(String message) {
        super(message);
    }
}
