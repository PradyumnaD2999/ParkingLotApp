package com.parkinglot.customexceptions;

public class NoAvailableSlotException extends Exception {
    public NoAvailableSlotException(String message) {
        super(message);
    }
}
