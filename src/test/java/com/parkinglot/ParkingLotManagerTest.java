package com.parkinglot;

import com.parkinglot.customexceptions.DuplicateParkingException;
import com.parkinglot.customexceptions.NoAvailableSlotException;
import com.parkinglot.customexceptions.VehicleNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParkingLotManagerTest {

    private ParkingLotManager parkingLotManager;

    // Use a new parking lot each test
    @BeforeEach
    void setUp() {
        // 9 total slots, 3 SMALL, 3 LARGE, 3 OVERSIZE
        parkingLotManager = new ParkingLotManager(9);
    }

    // Test if slot counts and parkedVehicles are as expected
    @Test
    public void testInitialSlotCounts() {
        System.out.println("\n--- Test: InitialSlotCounts ---");

        assertEquals(3, parkingLotManager.getSmallSlotCount());
        assertEquals(3, parkingLotManager.getLargeSlotCount());
        assertEquals(3, parkingLotManager.getOversizeSlotCount());
        assertTrue(parkingLotManager.getParkedVehicles().isEmpty());

        System.out.println("Free Slots: " + freeSlotStatus());
        System.out.println("Parked Vehicles: " + parkingLotManager.getParkedVehicles());
        System.out.println("Initialized parking lot.");
    }

    // Test vehicle parking
    @Test
    void testParkVehicleSuccess() throws NoAvailableSlotException, DuplicateParkingException {
        System.out.println("\n--- Test: ParkVehicleSuccess ---");

        // Park a small vehicle
        Vehicle vehicle = new Vehicle("CG25NG2506", SlotType.SMALL);
        parkingLotManager.parkVehicle(vehicle);

        // Get the small vehicle from parkedVehicles
        Map<String, SlotType> parked = parkingLotManager.getParkedVehicles();
        // Verify if parked in SMALL slot
        assertEquals(SlotType.SMALL, parked.get("CG25NG2506"));
        // Verify updated SMALL free slot count (initially 3, now 2)
        assertEquals(2, parkingLotManager.getSmallSlotCount());

        System.out.println("Free Slots: SMALL = " + parkingLotManager.getSmallSlotCount());
        System.out.println("Parked Vehicles: " + parkingLotManager.getParkedVehicles());
        System.out.println("Vehicle parked.");
    }

    // Test if vehicle is parked in next larger slot if slot of vehicle size is not available
    @Test
    void testParkMultipleWithSlotOverFlow() throws NoAvailableSlotException, DuplicateParkingException {
        System.out.println("\n--- Test: ParkMultipleWithSlotOverFlow ---");

        int i = 1, n = parkingLotManager.getSmallSlotCount();

        // Fill all SMALL slots
        while(i <= n)
            parkingLotManager.parkVehicle(new Vehicle("V" + i++, SlotType.SMALL));

        // This vehicle should go into LARGE slot since all small slots are filled
        parkingLotManager.parkVehicle(new Vehicle("V" + i, SlotType.SMALL));
        // Verify if parked in LARGE slot
        assertEquals(SlotType.LARGE, parkingLotManager.getParkedVehicles().get("V" + i));
        // Verify updated LARGE free slot count (initially 3, now 2)
        assertEquals(2, parkingLotManager.getLargeSlotCount());

        System.out.println("Free Slots: " + freeSlotStatus());
        System.out.println("Parked Vehicles: " + parkingLotManager.getParkedVehicles());
        System.out.println("Small vehicle parked in large slot.");
    }

    /* Test if vehicle keeps checking next larger slot if exact size slot and current larger slot is not available
    Small Vehicle tries to park in unavailable SMALL & LARGE slots, then parks in available OVERSIZE slot */
    @Test
    void testSmallVehicleFallsToOversize() throws NoAvailableSlotException, DuplicateParkingException {
        System.out.println("\n--- Test: SmallVehicleFallsToOversize ---");

        int i = 1, j = 1, n = parkingLotManager.getSmallSlotCount();

        // Fill all SMALL slots
        while(j++ <= n)
            parkingLotManager.parkVehicle(new Vehicle("S" + i++, SlotType.SMALL));

        j = 1;
        n = parkingLotManager.getLargeSlotCount();

        // Fill all LARGE slots
        while(j++ <= n)
            parkingLotManager.parkVehicle(new Vehicle("L" + i++, SlotType.LARGE));

        // Should be parked in OVERSIZE slot
        parkingLotManager.parkVehicle(new Vehicle("V" + i, SlotType.SMALL));
        // Verify if parked in OVERSIZE slot
        assertEquals(SlotType.OVERSIZE, parkingLotManager.getParkedVehicles().get("V" + i));
        // Verify updated OVERSIZE free slot count (initially 3, now 2)
        assertEquals(2, parkingLotManager.getOversizeSlotCount());

        System.out.println("Free Slots: " + freeSlotStatus());
        System.out.println("Parked Vehicles: " + parkingLotManager.getParkedVehicles());
        System.out.println("Small vehicle parked in oversize slot.");
    }

    // Test if a message is shown when no slot is available to park
    @Test
    void testNoSlotAvailableException() throws NoAvailableSlotException, DuplicateParkingException {
        System.out.println("\n--- Test: NoSlotAvailableException ---");

        int i = 1;
        int n = parkingLotManager.getSmallSlotCount() + parkingLotManager.getLargeSlotCount() + parkingLotManager.getOversizeSlotCount();

        // Fill all available slots (Will automatically get parked in next available larger slots)
        while(i <= n)
            parkingLotManager.parkVehicle(new Vehicle("CAR" + i++, SlotType.SMALL));

        // Make sure exception is thrown and get it
        NoAvailableSlotException ns = assertThrows(
                NoAvailableSlotException.class,
                () -> parkingLotManager.parkVehicle(new Vehicle("CARLAST", SlotType.SMALL))
        );

        // Check if expected message is shown
        assertEquals("No slot available for this vehicle type.", ns.getMessage());

        System.out.println("Free Slots: " + freeSlotStatus());
        System.out.println("Parked Vehicles: " + parkingLotManager.getParkedVehicles());
        System.out.println("All slots filled.");
    }

    // Test vehicle removal
    @Test
    void testRemoveVehicleSuccess() throws NoAvailableSlotException, VehicleNotFoundException, DuplicateParkingException {
        System.out.println("\n--- Test: RemoveVehicleSuccess ---");

        // Park a large vehicle
        Vehicle vehicle = new Vehicle("CG25FT0625", SlotType.LARGE);
        parkingLotManager.parkVehicle(vehicle);

        // Remove the vehicle from lot
        parkingLotManager.removeVehicle("CG25FT0625");

        // Vehicle should not be found in parkedVehicles
        assertFalse(parkingLotManager.isVehicleParked("CG25FT0625"));
        // Verify updated LARGE free slot count (initially 3, then 2, now finally 3)
        assertEquals(3, parkingLotManager.getLargeSlotCount());

        System.out.println("Free Slots: " + freeSlotStatus());
        System.out.println("Parked Vehicles: " + parkingLotManager.getParkedVehicles());
        System.out.println("Vehicle removed.");
    }

    // Test if a message is shown if vehicle is not found in parkedVehicles during removal
    @Test
    void testRemoveVehicleNotFound() {
        System.out.println("\n--- Test: RemoveVehicleNotFound ---");

        // Make sure exception is thrown and get it
        VehicleNotFoundException nf = assertThrows(
                VehicleNotFoundException.class,
                () -> parkingLotManager.removeVehicle("NOTFOUND123")
        );

        // Check if expected message is shown
        assertEquals("Vehicle not found.", nf.getMessage());

        System.out.println("Free Slots: " + freeSlotStatus());
        System.out.println("Parked Vehicles: " + parkingLotManager.getParkedVehicles());
        System.out.println("Vehicle not found.");
    }

    // Test if same vehicle CANNOT be parked again IF it already currently parked
    @Test
    void testDuplicateParkingIsIgnored() throws NoAvailableSlotException, DuplicateParkingException {
        System.out.println("\n--- Test: DuplicateParkingIsIgnored ---");

        // Park a vehicle
        Vehicle vehicle = new Vehicle("DUPLICATEPARK1234", SlotType.OVERSIZE);
        parkingLotManager.parkVehicle(vehicle);

        // Should not park again
        DuplicateParkingException dp = assertThrows(
                DuplicateParkingException.class,
                () -> parkingLotManager.parkVehicle(vehicle)
        );

        // Check if expected message is shown
        assertEquals("Vehicle is already parked.", dp.getMessage());

        /* Verify updated OVERSIZE free slot count
        Initially 3, after parking 1st time - 2, after attempting 2nd time - still 2) */
        assertEquals(2, parkingLotManager.getOversizeSlotCount());
        // Verify that there is only 1 parked vehicle
        assertEquals(1, parkingLotManager.getParkedVehicles().size());

        System.out.println("Free Slots: " + freeSlotStatus());
        System.out.println("Parked Vehicles: " + parkingLotManager.getParkedVehicles());
        System.out.println("Vehicle is already parked.");
    }

    // Test if same vehicle CANNOT be removed again IF it is not parked again after removing the first time
    @Test
    void testRemoveVehicleTwice() throws NoAvailableSlotException, DuplicateParkingException {
        System.out.println("\n--- Test: RemoveVehicleTwice ---");

        // Park a vehicle
        Vehicle vehicle = new Vehicle("DUPLICATEREMOVE4321", SlotType.LARGE);
        parkingLotManager.parkVehicle(vehicle);

        // Verify that exception is not thrown while removing parked car
        assertDoesNotThrow(() -> parkingLotManager.removeVehicle("DUPLICATEREMOVE4321"));

        // Make sure exception is thrown and get it
        VehicleNotFoundException nf = assertThrows(
                VehicleNotFoundException.class,
                () -> parkingLotManager.removeVehicle("DUPLICATEREMOVE4321")
        );

        // Verify if expected message is shown
        assertEquals("Vehicle not found.", nf.getMessage());

        System.out.println("Free Slots: " + freeSlotStatus());
        System.out.println("Parked Vehicles: " + parkingLotManager.getParkedVehicles());
        System.out.println("Vehicle is not parked. Cannot remove.");
    }

    /* LARGE vehicle should not park in SMALL slot in case of unavailable LARGE and OVERSIZE slots
    OVERSIZE vehicles will have same working: If OVERSIZE slot is not available, should not try to park in LARGE and SMALL slots*/
    @Test
    void testLargeVehicleCannotParkInSmall() throws NoAvailableSlotException, DuplicateParkingException {
        System.out.println("\n--- Test: LargeVehicleCannotParkInSmall ---");

        int i = 1, j = 1, n = parkingLotManager.getLargeSlotCount();

        // Fill all LARGE slots
        while(j++ <= n)
            parkingLotManager.parkVehicle(new Vehicle("L" + i++, SlotType.LARGE));

        j = 1;
        n = parkingLotManager.getOversizeSlotCount();

        // Fill all OVERSIZE slots
        while(j++ <= n)
            parkingLotManager.parkVehicle(new Vehicle("O" + i++, SlotType.OVERSIZE));

        // Make sure exception is thrown and get it
        NoAvailableSlotException ns = assertThrows(
                NoAvailableSlotException.class,
                () -> parkingLotManager.parkVehicle(new Vehicle("LARGE123", SlotType.LARGE))
        );

        // Verify expected message is shown
        assertEquals("No slot available for this vehicle type.", ns.getMessage());

        System.out.println("Free Slots: " + freeSlotStatus());
        System.out.println("Parked Vehicles: " + parkingLotManager.getParkedVehicles());
        System.out.println("No large or oversize slot available and large vehicle cannot be parked in small slot.");
    }

    // Helper method to build slot status string
    private String freeSlotStatus() {
        return "SMALL=" + parkingLotManager.getSmallSlotCount() +
                ", LARGE=" + parkingLotManager.getLargeSlotCount() +
                ", OVERSIZE=" + parkingLotManager.getOversizeSlotCount();
    }
}