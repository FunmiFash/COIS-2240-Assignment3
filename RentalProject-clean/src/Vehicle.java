public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    private String capitalize_vai(String input) {
        if (input == null || input.isEmpty()) return input;

        return input.substring(0,1).toUpperCase() +
               input.substring(1).toLowerCase();
    }

    private boolean isValidPlate_vai(String plate) {
        if (plate == null || plate.isEmpty()) return false;
        return plate.matches("[A-Z]{3}[0-9]{3}");
    }

    public Vehicle(String make, String model, int year) {
        this.make = capitalize_vai(make);
        this.model = capitalize_vai(model);
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    public Vehicle() {
        this(null, null, 0);
    }

    public void setLicensePlate(String plate) {
        if (!isValidPlate_vai(plate)) {
            throw new IllegalArgumentException("Invalid license plate format");
        }
        this.licensePlate = plate.toUpperCase();
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
}