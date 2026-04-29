package org.test.parking.exception;

public class NoAvailableSlotsException extends Exception {
    public NoAvailableSlotsException(String message) {
        super(message);
    }
}
