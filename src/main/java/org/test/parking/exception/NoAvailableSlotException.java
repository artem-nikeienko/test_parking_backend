package org.test.parking.exception;

public class NoAvailableSlotException extends Exception {
    public NoAvailableSlotException(String message) {
        super(message);
    }
}
