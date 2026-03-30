import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.time.LocalDate;

public class RentalSystem {

    private static RentalSystem instance_vai;

    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    // ✅ Singleton constructor
    private RentalSystem() {
        loadData_vai();
    }

    public static RentalSystem getInstance_vai() {
        if (instance_vai == null) {
            instance_vai = new RentalSystem();
        }
        return instance_vai;
    }

    // ✅ ADD VEHICLE
    public boolean addVehicle_vai(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Duplicate vehicle!");
            return false;
        }

        vehicles.add(vehicle);
        saveVehicle_vai(vehicle);
        return true;
    }

    // ✅ ADD CUSTOMER
    public boolean addCustomer_vai(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Duplicate customer!");
            return false;
        }

        customers.add(customer);
        saveCustomer_vai(customer);
        return true;
    }

    // ✅ RENT VEHICLE
    public boolean rentVehicle_vai(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() != Vehicle.VehicleStatus.Available) {
            return false;
        }

        vehicle.setStatus(Vehicle.VehicleStatus.Rented);

        RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
        rentalHistory.addRecord(record);
        saveRecord_vai(record);

        rewriteVehiclesFile_vai();

        return true;
    }

    // ✅ RETURN VEHICLE
    public boolean returnVehicle_vai(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() != Vehicle.VehicleStatus.Rented) {
            return false;
        }

        vehicle.setStatus(Vehicle.VehicleStatus.Available);

        RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
        rentalHistory.addRecord(record);
        saveRecord_vai(record);

        rewriteVehiclesFile_vai();

        return true;
    }

    // ✅ DISPLAY VEHICLES
    public void displayVehicles(Vehicle.VehicleStatus status) {
        System.out.println("\n=== " + (status == null ? "All" : status) + " Vehicles ===");

        for (Vehicle v : vehicles) {
            if (status == null || v.getStatus() == status) {
                System.out.println(v.getInfo());
            }
        }
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println(c.getCustomerId() + " - " + c.getCustomerName());
        }
    }

    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("No rental history found.");
            return;
        }

        for (RentalRecord r : rentalHistory.getRentalHistory()) {
            System.out.println(
                r.getRecordType() + " | " +
                r.getVehicle().getLicensePlate() + " | " +
                r.getCustomer().getCustomerName() + " | " +
                r.getRecordDate() + " | $" +
                r.getTotalAmount()
            );
        }
    }

    // ✅ FIND METHODS
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == id) {
                return c;
            }
        }
        return null;
    }

    // ✅ SAVE METHODS
    public void saveVehicle_vai(Vehicle vehicle) {
        try (FileWriter writer = new FileWriter("vehicles.txt", true)) {
            writer.write(
                vehicle.getLicensePlate() + "," +
                vehicle.getMake() + "," +
                vehicle.getModel() + "," +
                vehicle.getYear() + "," +
                vehicle.getStatus() + "\n"
            );
        } catch (Exception e) {
            System.out.println("Error saving vehicle.");
        }
    }

    public void saveCustomer_vai(Customer customer) {
        try (FileWriter writer = new FileWriter("customers.txt", true)) {
            writer.write(
                customer.getCustomerId() + "," +
                customer.getCustomerName() + "\n"
            );
        } catch (Exception e) {
            System.out.println("Error saving customer.");
        }
    }

    public void saveRecord_vai(RentalRecord record) {
        try (FileWriter writer = new FileWriter("rental_records.txt", true)) {
            writer.write(
                record.getRecordType() + "," +
                record.getVehicle().getLicensePlate() + "," +
                record.getCustomer().getCustomerId() + "," +
                record.getRecordDate() + "," +
                record.getTotalAmount() + "\n"
            );
        } catch (Exception e) {
            System.out.println("Error saving rental record.");
        }
    }

    // ✅ REWRITE VEHICLE FILE
    private void rewriteVehiclesFile_vai() {
        try (FileWriter writer = new FileWriter("vehicles.txt", false)) {
            for (Vehicle v : vehicles) {
                writer.write(
                    v.getLicensePlate() + "," +
                    v.getMake() + "," +
                    v.getModel() + "," +
                    v.getYear() + "," +
                    v.getStatus() + "\n"
                );
            }
        } catch (Exception e) {
            System.out.println("Error updating vehicles file.");
        }
    }

    // ✅ LOAD DATA
    public void loadData_vai() {
        loadVehicles_vai();
        loadCustomers_vai();
        loadRecords_vai();
    }

    public void loadVehicles_vai() {
        try {
            File file = new File("vehicles.txt");
            if (!file.exists()) return;

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");

                String plate = data[0];
                String make = data[1];
                String model = data[2];
                int year = Integer.parseInt(data[3]);

                Vehicle v = new Car(make, model, year, 4);
                v.setLicensePlate(plate);

                vehicles.add(v);
            }

            scanner.close();
        } catch (Exception e) {
            System.out.println("Error loading vehicles.");
        }
    }

    public void loadCustomers_vai() {
        try {
            File file = new File("customers.txt");
            if (!file.exists()) return;

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");

                int id = Integer.parseInt(data[0]);
                String name = data[1];

                customers.add(new Customer(id, name));
            }

            scanner.close();
        } catch (Exception e) {
            System.out.println("Error loading customers.");
        }
    }

    public void loadRecords_vai() {
        try {
            File file = new File("rental_records.txt");
            if (!file.exists()) return;

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(",");

                String type = data[0];
                String plate = data[1];
                int customerId = Integer.parseInt(data[2]);
                LocalDate date = LocalDate.parse(data[3]);
                double amount = Double.parseDouble(data[4]);

                Vehicle v = findVehicleByPlate(plate);
                Customer c = findCustomerById(customerId);

                if (v != null && c != null) {
                    rentalHistory.addRecord(new RentalRecord(v, c, date, amount, type));
                }
            }

            scanner.close();
        } catch (Exception e) {
            System.out.println("Error loading rental records.");
        }
    }
}