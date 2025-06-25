# Parking Lot Management Application

## Overview
This is a menu-driven command-line-based Parking Lot Management System.
The application allows dynamic management of a parking lot with configurable slots and vehicle types. It supports vehicle entry, exit, parking status display, and resetting the praking lot.



## Running the Application
### To run using the JAR file (Easy and convenient):
#### Requirements:
- Java 17 or higher

#### How to Run:
1. Find the JAR file in:
   `ParkingLotApp/release/`
2. Open terminal or command prompt 
3. Navigate to directory containing the JAR file
4. Run:
```
   java -jar ParkingLotApp-1.0-SNAPSHOT.jar
```

### To build and run (Complex than JAR file):
```
mvn clean install
mvn exec:java -Dexec.mainClass="com.parkinglot.Main"
```

Alternatively, if `mainClass` is configured in `pom.xml` for `exec-maven-plugin`:
```
mvn clean install
mvn exec:java
```

Ensure `exec-maven-plugin` is configured in `pom.xml`.



## Features Implemented
- Supports 3 vehicle and slot sizes: SMALL, LARGE, OVERSIZE
- Vehicles are parked based on slot size availability and fallback rules
  (If exact slot size is not available, check slot availability in next greater slot size)
- Efficient slot management using HashMap and EnumMap
  (EnumMap to maintain each slot size's availability count and HashMap to maintain currently parked cars in a specific slot type)
- Logging using SLF4J + Logback (Logs written to a file, no `System.out.println()` for user operations)
- Menu-driven CLI
- Exception handling with custom exceptions
- JUnit 5 for unit tests
- Maven project with code quality tools:
   - SpotBugs
   - PMD
   - Checkstyle



## Tech Stack
- Java 17
- Maven
- JUnit 5
- SLF4J + Logback
- SpotBugs, PMD, Checkstyle



## Project Structure
```
ParkingLotApp
├─logs/
│    ├─ parking_lot.log
├─src/
│    ├─ main/
│    │   ├─ java/
│    │   │   └─ com/parkinglot/
│    │   │       ├─ customexceptions/
│    │   │       │    ├─ DuplicateParkingLotException.java
│    │   │       │    ├─ NoAvailableSlotException.java
│    │   │       │    ├─ VehicleNotFound.java
│    │   │       ├─ utils/
│    │   │       │    ├─ LoggerConfig.java
│    │   │       ├─ ParkingLotManager.java
│    │   │       ├─ Vehicle.java
│    │   │       ├─ SlotType.java
│    │   │       ├─ Main.java
│    │   └─ resources/
│    │       └─ logback.xml
│    ├─ test/
│    │   └─ java/
│    │       └─ com/parkinglot/
│    │           └─ ParkingLotManagerTest.java
```


## Testing
Run all unit tests:
```
mvn test
```

### Manually Verified Tests
- Display status when lot is empty       
- Display status with vehicles parked



## Logs
- Logs are written to `logs/parking_lot.log`.
- Errors, warnings, and info logs for operations are available there.
- Debug logs are currently disabled.


## Future Scope
- Add state persistence using Java Serialization or JSON
- Add Slot-ID mapping if required for deeper functionality and analysis
- Convert CLI to Web UI
- Use Database for persistence in scalable deployments
