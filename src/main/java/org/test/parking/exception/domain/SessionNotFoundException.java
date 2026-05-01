package org.test.parking.exception.domain;

public class SessionNotFoundException extends DomainException {
    public SessionNotFoundException(String sessionId) {
        super(String.format("Session with ID [%s] not found", sessionId));
    }
}
