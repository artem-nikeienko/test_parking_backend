package org.test.parking.exception.domain;

public class SessionNotFoundException extends DomainException {
    public SessionNotFoundException(String message) {
        super(message);
    }
}
