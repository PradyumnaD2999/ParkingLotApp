package com.parkinglot.customexceptions;

public class DuplicateParkingException extends Exception {
    public DuplicateParkingException(String message) {
        super(message);
    }
}
