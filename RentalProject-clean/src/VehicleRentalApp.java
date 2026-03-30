import java.util.Scanner;
import java.time.LocalDate;

public class VehicleRentalApp {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        RentalSystem rentalSystem = RentalSystem.getInstance_vai(); // Singleton already loads data

        while (true) {
            System.out.println("\n1: Add Vehicle\n" +
                               "2: Add Customer\n" +
                               "3: Rent Vehicle\n" +
                               "4: Return Vehicle\n" +
                               "5: Display Available Vehicles\n" +
                               "6: Show Rental History\n" +
                               "0: Exit\n");

            int choice;

            try {
                choice = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("❌ Please enter a valid number!");
                scanner.nextLine();
                continue;
            }

            scanner.nextLine(); 

            switch (choice) {

                case 1:
                    System.out.println("  1: Car\n" +
                                       "  2: Minibus\n" +
                                       "  3: Pickup Truck");

                    int type;
                    try {
                        type = scanner.nextInt();
                    } catch (Exception e) {
                        System.out.println("❌ Invalid type!");
                        scanner.nextLine();
                        break;
                    }
                    scanner.nextLine();

                    System.out.print("Enter license plate: ");
                    String plate = scanner.nextLine().toUpperCase();

                    System.out.print("Enter make: ");
                    String make = scanner.nextLine();

                    System.out.print("Enter model: ");
                    String model = scanner.nextLine();

                    System.out.print("Enter year: ");
                    int year = scanner.nextInt();
                    scanner.nextLine();

                    Vehicle vehicle = null;

                    if (type == 1) {
                        System.out.print("Enter number of seats: ");
                        int seats = scanner.nextInt();
                        scanner.nextLine();
                        vehicle = new Car(make, model, year, seats);

                    } else if (type == 2) {
                        System.out.print("Is accessible? (true/false): ");
                        boolean isAccessible = scanner.nextBoolean();
                        scanner.nextLine();
                        vehicle = new Minibus(make, model, year, isAccessible);

                    } else if (type == 3) {
                        System.out.print("Enter cargo size: ");
                        double cargoSize = scanner.nextDouble();
                        scanner.nextLine();

                        System.out.print("Has trailer? (true/false): ");
                        boolean hasTrailer = scanner.nextBoolean();
                        scanner.nextLine();

                        vehicle = new PickupTruck(make, model, year, cargoSize, hasTrailer);
                    }

                    if (vehicle != null) {
                        vehicle.setLicensePlate(plate);
                        rentalSystem.addVehicle(vehicle);
                        System.out.println("✅ Vehicle added successfully.");
                    } else {
                        System.out.println("❌ Invalid vehicle type.");
                    }
                    break;

                case 2:
                    try {
                        System.out.print("Enter customer ID (number): ");
                        int cid = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Enter name: ");
                        String cname = scanner.nextLine();

                        rentalSystem.addCustomer(new Customer(cid, cname));
                        System.out.println("✅ Customer added successfully.");

                    } catch (Exception e) {
                        System.out.println("❌ Invalid input!");
                        scanner.nextLine();
                    }
                    break;

                case 3:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Available);

                    System.out.print("Enter license plate: ");
                    String rentPlate = scanner.nextLine().toUpperCase();

                    System.out.println("Registered Customers:");
                    rentalSystem.displayAllCustomers();

                    int cidRent;
                    try {
                        System.out.print("Enter customer ID (number): ");
                        cidRent = scanner.nextInt();
                    } catch (Exception e) {
                        System.out.println("❌ Enter a valid number!");
                        scanner.nextLine();
                        break;
                    }
                    scanner.nextLine();

                    System.out.print("Enter rental amount: ");
                    double rentAmount = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle vehicleToRent = rentalSystem.findVehicleByPlate(rentPlate);
                    Customer customerToRent = rentalSystem.findCustomerById(cidRent);

                    if (vehicleToRent == null || customerToRent == null) {
                        System.out.println("❌ Vehicle or customer not found.");
                        break;
                    }

                    if (vehicleToRent.getStatus() != Vehicle.VehicleStatus.Available) {
                        System.out.println("❌ Vehicle is not available.");
                        break;
                    }

                    rentalSystem.rentVehicle(vehicleToRent, customerToRent, LocalDate.now(), rentAmount);
                    break;

                case 4:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Rented);

                    System.out.print("Enter license plate: ");
                    String returnPlate = scanner.nextLine().toUpperCase();

                    System.out.println("Registered Customers:");
                    rentalSystem.displayAllCustomers();

                    int cidReturn;
                    try {
                        System.out.print("Enter customer ID (number): ");
                        cidReturn = scanner.nextInt();
                    } catch (Exception e) {
                        System.out.println("❌ Enter a valid number!");
                        scanner.nextLine();
                        break;
                    }
                    scanner.nextLine();

                    System.out.print("Enter return fees: ");
                    double returnFees = scanner.nextDouble();
                    scanner.nextLine();

                    Vehicle vehicleToReturn = rentalSystem.findVehicleByPlate(returnPlate);
                    Customer customerToReturn = rentalSystem.findCustomerById(cidReturn);

                    if (vehicleToReturn == null || customerToReturn == null) {
                        System.out.println("❌ Vehicle or customer not found.");
                        break;
                    }

                    rentalSystem.returnVehicle(vehicleToReturn, customerToReturn, LocalDate.now(), returnFees);
                    break;

                case 5:
                    rentalSystem.displayVehicles(Vehicle.VehicleStatus.Available);
                    break;

                case 6:
                    rentalSystem.displayRentalHistory();
                    break;

                case 0:
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("❌ Invalid option.");
            }
        }
    }
}