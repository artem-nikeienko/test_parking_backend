package org.test.parking.exception.domain;

public class SessionAlreadyCompletedException extends DomainException {
    public SessionAlreadyCompletedException(String sessionId) {
        super(String.format("Session with ID [%s] is already completed.", sessionId));
    }
}
