// ── Step 5: Client — RideDispatcher ──────────────────────────────────────────
public class RideDispatcher {

    public void bookRide(VehicleType type, String pickup, String drop, double distanceKm) {
        // Client only knows Vehicle interface — not Bike, Auto, or Cab
        Vehicle vehicle = VehicleFactory.create(type);

        vehicle.dispatch(pickup, drop);

        double fare = vehicle.calculateFare(distanceKm);
        System.out.printf("       Estimated Fare for %.1f km: ₹%.2f%n%n", distanceKm, fare);
    }

    public static void main(String[] args) {
        RideDispatcher dispatcher = new RideDispatcher();

        dispatcher.bookRide(VehicleType.BIKE, "Koregaon Park", "Viman Nagar",  4.5);
        dispatcher.bookRide(VehicleType.AUTO, "Shivaji Nagar", "Kothrud",      7.2);
        dispatcher.bookRide(VehicleType.CAB,  "Pune Airport",  "Hinjewadi",   18.0);
    }
}