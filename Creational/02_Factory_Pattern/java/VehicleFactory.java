// Scenario: Vehicle Dispatcher — Ride-Sharing App
// Dispatcher asks factory for a vehicle. Factory returns the right one.

/**
 * A ride-sharing platform (like Uber) needs to dispatch different vehicle types
 * — Bike, Auto, and Cab — based on what the rider selects.
 * The dispatcher never constructs vehicles directly;
 * it delegates entirely to the factory.
 */

// ── Step 1: Common Interface ──────────────────────────────────────────────────

interface Vehicle {
    String getType();

    double calculateFare(double distanceKm);

    void dispatch(String pickupLocation, String dropLocation);
}

class Bike implements Vehicle {
    private static final double RATE_PER_KM = 8.0;

    @Override
    public String getType() {
        return "BIKE";
    }

    @Override
    public double calculateFare(double distanceKm) {
        return RATE_PER_KM * distanceKm;
    }

    @Override
    public void dispatch(String pickup, String drop) {
        System.out.printf("[BIKE] 🏍️  Dispatched from '%s' to '%s' | Rate: ₹%.1f/km%n",
                pickup, drop, RATE_PER_KM);
    }
}

class Auto implements Vehicle {
    private static final double RATE_PER_KM = 12.0;

    @Override
    public String getType() {
        return "AUTO";
    }

    @Override
    public double calculateFare(double distanceKm) {
        return RATE_PER_KM * distanceKm;
    }

    @Override
    public void dispatch(String pickup, String drop) {
        System.out.printf("[AUTO] 🛺  Dispatched from '%s' to '%s' | Rate: ₹%.1f/km%n",
                pickup, drop, RATE_PER_KM);
    }
}

class Cab implements Vehicle {
    private static final double BASE_FARE = 50.0;
    private static final double RATE_PER_KM = 18.0;

    @Override
    public String getType() {
        return "CAB";
    }

    @Override
    public double calculateFare(double distanceKm) {
        return BASE_FARE + (RATE_PER_KM * distanceKm);
    }

    @Override
    public void dispatch(String pickup, String drop) {
        System.out.printf("[CAB]  🚕  Dispatched from '%s' to '%s' | Base: ₹%.1f + ₹%.1f/km%n",
                pickup, drop, BASE_FARE, RATE_PER_KM);
    }
}

// ── Step 3: Vehicle Type Enum ─────────────────────────────────────────────────

enum VehicleType {
    BIKE, AUTO, CAB
}

// ── Step 4: The Factory ────────────────────────────────────────────────────────

public class VehicleFactory {
    /**
     * Returns the appropriate Vehicle implementation.
     * Client code is completely decoupled from Bike, Auto, and Cab constructors.
     */
    public static Vehicle create(VehicleType type) {
        return switch (type) {
            case BIKE -> new Bike();
            case AUTO -> new Auto();
            case CAB  -> new Cab();
        };
        // Java 14+ switch expression is exhaustive on enums —
        // no default needed, compiler enforces all cases are handled.
    }
}
