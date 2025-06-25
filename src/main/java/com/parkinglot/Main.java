package com.parkinglot;

import com.parkinglot.customexceptions.DuplicateParkingException;
import com.parkinglot.customexceptions.NoAvailableSlotException;
import com.parkinglot.customexceptions.VehicleNotFoundException;
import com.parkinglot.utils.LoggerConfig;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerConfig.getLogger(Main.class);

    private static ParkingLotManager parkingLotManager;
    private static Scanner sc;

    public static void main(String[] args) {
        sc = new Scanner(System.in, StandardCharsets.UTF_8);
        System.out.println("Welcome to Parking Lot Management System");

        // Initialize parking lot
        initParkingLot();

        // CLI Menu (Loops until user selects exit)
        while (true) {
            System.out.println("\nChoose your action:");
            System.out.println("1. Park Vehicle");
            System.out.println("2. Remove Vehicle");
            System.out.println("3. Display Status");
            System.out.println("4. Reset Parking Lot");
            System.out.println("5. Exit");

            System.out.print("\nYour choice (Enter number): ");
            String input = sc.nextLine();

            switch (input) {
                case "1":
                    handleParking();
                    break;

                case "2":
                    handleRemoval();
                    break;

                case "3":
                    parkingLotManager.displayStatus();
                    break;

                case "4":
                    handleReset();
                    break;

                case "5":
                    System.out.println("Exiting the application. Goodbye!");
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Create a parking lot with "n" slots
    private static void initParkingLot() {
        int slots = 0;

        // Loop until user provides positive "n"
        while (slots <= 0) {
            System.out.print("Enter total number of parking slots: ");

            // Try-catch block handles non-numeric inputs
            try {
                slots = Integer.parseInt(sc.nextLine());

                // Handle negative count
                if (slots <= 0)
                    System.out.println("Please enter a positive number.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }

        parkingLotManager = new ParkingLotManager(slots);
        System.out.println("Parking lot created with " + slots + " total slots.");
        logger.info("Parking lot created with {} total slots.", slots);
    }

    // Park a vehicle
    private static void handleParking() {
        String number;

        while (true) {
            System.out.print("Enter Vehicle Number: ");
            number = sc.nextLine();

            if (number != null && !number.trim().isEmpty())
                break;

            System.out.println("Vehicle number cannot be null or empty. Please try again.");
        }

        SlotType type;

        while(true) {
            System.out.println("\nSelect Vehicle Size:");
            System.out.println("1. SMALL (Small and compact car)");
            System.out.println("2. LARGE (Full-size car)");
            System.out.println("3. OVERSIZE (SUV or Truck)");

            System.out.print("\nYour choice (Enter number): ");
            String size = sc.nextLine();

            // Map size of vehicle
            try {
                type = switch (size) {
                    case "1" -> SlotType.SMALL;
                    case "2" -> SlotType.LARGE;
                    case "3" -> SlotType.OVERSIZE;
                    default -> throw new IllegalArgumentException();
                };

                // Create and park vehicle with number and size
                Vehicle vehicle = new Vehicle(number, type);
                parkingLotManager.parkVehicle(vehicle);
                System.out.println("Successfully parked vehicle: " + number);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            } catch (NoAvailableSlotException | DuplicateParkingException e) {
                System.out.println("Error: " + e.getMessage());
                break;
            }
        }
    }

    // Remove a vehicle from parking lot
    private static void handleRemoval() {
        String number;

        while (true) {
            System.out.print("Enter Vehicle Number: ");
            number = sc.nextLine();

            if (number != null && !number.trim().isEmpty())
                break;

            System.out.println("Vehicle number cannot be null or empty. Please try again.");
        }

        try {
            parkingLotManager.removeVehicle(number);
            System.out.println("Successfully removed vehicle from parking: " + number);
        } catch (VehicleNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Reset the entire parking lot. (Basically a new parking lot will be created)
    private static void handleReset() {
        System.out.print("Are you sure you want to reset the parking lot? (Y/N): ");
        String confirm = sc.nextLine().toUpperCase();

        if (confirm.equals("Y")) {
            initParkingLot();
        } else {
            System.out.println("Reset cancelled.");
        }
    }
}