package com.parkinglot;

import com.parkinglot.customexceptions.DuplicateParkingException;
import com.parkinglot.customexceptions.NoAvailableSlotException;
import com.parkinglot.customexceptions.VehicleNotFoundException;
import com.parkinglot.utils.LoggerConfig;
import org.slf4j.Logger;

import java.util.*;

public class ParkingLotManager {
    private static final Logger logger = LoggerConfig.getLogger(ParkingLotManager.class);

    // Stores available slot count of each SlotType
    private Map<SlotType, Integer> freeSlots;

    // Stores vehicleNumber and SlotType parked in
    private Map<String, SlotType> parkedVehicles;

    // Initialize
    public ParkingLotManager(int totalSlots) {
        this.parkedVehicles = new HashMap<>();
        this.freeSlots = new EnumMap<>(SlotType.class);

        int slotCount = totalSlots / 3;
        int remaining = totalSlots % 3;

        // Divide total slots between different sizes.
        // Extra slots go to oversize slots
        int i = 0;
        for(SlotType type : SlotType.values()) {
            freeSlots.put(type, slotCount);

            if(i == SlotType.values().length - 1)
                freeSlots.put(type, freeSlots.get(type) + remaining);

            i++;
        }
    }

    /* Parks a vehicle based on its size.
       Tries to assign the smallest available slot that can fit the vehicle.
       Throws NoAvailableSlotException if none are available. */
    public void parkVehicle(Vehicle vehicle) throws NoAvailableSlotException, DuplicateParkingException {
        SlotType vehicleSize = vehicle.getSize();
        String vehicleNumber = vehicle.getVehicleNumber();

        // If vehicle is already parked in a slot, it cant be parked in two different slots at a time
        if (parkedVehicles.containsKey(vehicleNumber)) {
            logger.warn("Vehicle {} is already parked in {} slot.", vehicleNumber, parkedVehicles.get(vehicleNumber));
            throw new DuplicateParkingException("Vehicle is already parked.");
        }

        // If there is a free slot of exact size as vehicle's size, park it in one of that size's slots
        if(tryParking(vehicleNumber, vehicleSize))
            return;


        /* If the vehicle size is small and small slots are not available, check if large slots are available
        AND if there is a free large slot, park the small vehicle in one of the slots. */
        if(vehicleSize == SlotType.SMALL && tryParking(vehicleNumber, SlotType.LARGE))
            return;

        /* If the vehicle size is small or large and small and large slots are not available,
        check if oversize slots are available AND if there is a free oversize slot,
        park the small or large vehicle in one of the slots */
        if((vehicleSize == SlotType.SMALL || vehicleSize == SlotType.LARGE) && tryParking(vehicleNumber, SlotType.OVERSIZE))
            return;

        // If no slots are available, throw exception that no slot is available
        logger.error("No available slot for vehicle {}", vehicleNumber);
        throw new NoAvailableSlotException("No slot available for this vehicle type.");
    }

    // Attempts to park the vehicle into the specified slot size. Returns true if successful, false otherwise.
    public boolean tryParking(String vehicleNumber, SlotType size) {
        if(freeSlots.get(size) > 0) {
            // Park the vehicle
            parkedVehicles.put(vehicleNumber, size);

            // Decrease free slot count by 1
            freeSlots.put(size, freeSlots.get(size) - 1);

            logger.info("Parked vehicle {} in {} slot", vehicleNumber, size);

            return true;
        }

        return false;
    }

    // Removes a parked vehicle. Throws VehicleNotFoundException if vehicle is not parked.
    public void removeVehicle(String vehicleNumber) throws VehicleNotFoundException {
        // If vehicle is not parked in any slot, impossible to remove it
        if (!isVehicleParked(vehicleNumber)) {
            logger.error("Vehicle {} not found in the parking lot.", vehicleNumber);
            throw new VehicleNotFoundException("Vehicle not found.");
        }

        // Increase the slot count of the slot type in which vehicle is parked to mark it as free
        SlotType freedSlot = parkedVehicles.get(vehicleNumber);
        freeSlots.put(freedSlot, freeSlots.get(freedSlot) + 1);

        // Remove the vehicle from parking
        parkedVehicles.remove(vehicleNumber);

        logger.info("Vehicle {} removed from {} slot.", vehicleNumber, freedSlot);
    }

    // Displays the current status of the parking lot.
    public void displayStatus() {
        System.out.println("===== Parking Lot Status =====");

        // Display the count of available slot sizes (SMALL, LARGE, OVERSIZE)
        System.out.println("Available Small Slots: " + freeSlots.get(SlotType.SMALL));
        System.out.println("Available Large Slots: " + freeSlots.get(SlotType.LARGE));
        System.out.println("Available Oversize Slots: " + freeSlots.get(SlotType.OVERSIZE));

        System.out.println("\nParked Vehicles:");

        // Display the status of currently parked vehicles (which vehicle in which sized slot)
        if (parkedVehicles.isEmpty()) {
            System.out.println("No vehicles currently parked.");
        } else {
            for (Map.Entry<String, SlotType> entry : parkedVehicles.entrySet()) {
                System.out.println("Vehicle: " + entry.getKey() + " | Parked In Slot Type: " + entry.getValue());
            }
        }

        System.out.println("================================");
    }

    // Checks id a vehicle is currently parked
    public boolean isVehicleParked(String vehicleNumber) {
        return parkedVehicles.containsKey(vehicleNumber);
    }

    // Getters for unit testing and persistence
    public int getSmallSlotCount() {
        return freeSlots.get(SlotType.SMALL);
    }

    public int getLargeSlotCount() {
        return freeSlots.get(SlotType.LARGE);
    }

    public int getOversizeSlotCount() {
        return freeSlots.get(SlotType.OVERSIZE);
    }

    public Map<String, SlotType> getParkedVehicles() {
        return Map.copyOf(parkedVehicles);
    }
}
