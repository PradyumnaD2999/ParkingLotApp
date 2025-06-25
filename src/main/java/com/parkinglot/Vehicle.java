package com.parkinglot;

public class Vehicle {
    private String vehicleNumber;
    private SlotType vehicleSize;

    public Vehicle(String vehicleNumber, SlotType vehicleSize) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleSize = vehicleSize;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public SlotType getSize() {
        return vehicleSize;
    }
}
