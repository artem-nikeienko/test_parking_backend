package org.test.parking.exception.domain;

public class LotNotFoundException extends DomainException {
    public LotNotFoundException(String message) {
        super(message);
    }
}
