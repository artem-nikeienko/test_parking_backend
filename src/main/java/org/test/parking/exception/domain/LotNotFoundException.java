package org.test.parking.exception.domain;

public class LotNotFoundException extends DomainException {
    public LotNotFoundException(String lotId) {
        super(String.format("Lot with id [%s] was not found", lotId));
    }
}
