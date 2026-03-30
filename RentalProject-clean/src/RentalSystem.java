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

    private RentalSystem() {
        loadData_vai(); 
    }

    public static RentalSystem getInstance_vai() {
        if (instance_vai == null) {
            instance_vai = new RentalSystem();
        }
        return instance_vai;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle_vai(vehicle);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer_vai(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);

            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRental_vai(record);

            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);

            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRental_vai(record);

            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

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

    public void saveVehicle_vai(Vehicle v) {
        try (FileWriter fw = new FileWriter("vehicles.txt", true)) {
            fw.write(v.getLicensePlate() + "," +
                     v.getMake() + "," +
                     v.getModel() + "," +
                     v.getYear() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveCustomer_vai(Customer c) {
        try (FileWriter fw = new FileWriter("customers.txt", true)) {
            fw.write(c.getId() + "," + c.getName() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveRecord_vai(RentalRecord r) {
        try (FileWriter fw = new FileWriter("rental_records.txt", true)) {
            fw.write(r.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

            writer.close();
        } catch (Exception e) {
            System.out.println("Error saving rental history.");
        }
    }

    public void loadData_vai() {
        loadVehicles_vai();
        loadCustomers_vai();
        loadRentalHistory_vai();
    }

    public void loadVehicles_vai() {
        try {
            File file = new File("vehicles.txt");
            if (!file.exists()) return;

            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.trim().isEmpty()) continue;

                String[] data = line.split(",");

                if (data.length < 5) continue; // prevents crash

                try {
                    String plate = data[0];
                    String make = data[1];
                    String model = data[2];
                    int year = Integer.parseInt(data[3]);

                    Vehicle v = new Car(make, model, year, 4);
                    v.setLicensePlate(plate);

                    vehicles.add(v);

                } catch (Exception e) {
                    System.out.println("Skipping bad line in vehicles.txt");
                }
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

    public void loadRentalHistory_vai() {
        try {
            File file = new File("rentalHistory.txt");
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
            System.out.println("Error loading rental history.");
        }
    }
}