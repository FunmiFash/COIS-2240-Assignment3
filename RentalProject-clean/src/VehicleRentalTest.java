import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

public class VehicleRentalTest {

    private RentalSystem system;

    @BeforeEach
    void setUp() {
        system = RentalSystem.getInstance_vai();
    }

    @Test
    void testLicensePlate_vai() {

        Vehicle v = new Car("Toyota", "Corolla", 2019, 4);

        assertDoesNotThrow(() -> v.setLicensePlate("AAA100"));
        assertDoesNotThrow(() -> v.setLicensePlate("ABC567"));
        assertDoesNotThrow(() -> v.setLicensePlate("ZZZ999"));

        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> v.setLicensePlate("ZZZ99"));
    }

    @Test
    void testRentAndReturnVehicle_vai() {

        Vehicle v = new Car("Toyota", "Corolla", 2019, 4);
        v.setLicensePlate("AAA111");

        Customer c = new Customer(1, "George");

        system.addVehicle_vai(v);
        system.addCustomer_vai(c);

        assertEquals(Vehicle.VehicleStatus.Available, v.getStatus());

        assertTrue(system.rentVehicle_vai(v, c, LocalDate.now(), 100));
        assertEquals(Vehicle.VehicleStatus.Rented, v.getStatus());

        assertFalse(system.rentVehicle_vai(v, c, LocalDate.now(), 100));

        assertTrue(system.returnVehicle_vai(v, c, LocalDate.now(), 20));
        assertEquals(Vehicle.VehicleStatus.Available, v.getStatus());

        assertFalse(system.returnVehicle_vai(v, c, LocalDate.now(), 20));
    }

    @Test
    void testSingletonRentalSystem_vai() throws Exception {

        Constructor<RentalSystem> constructor =
                RentalSystem.class.getDeclaredConstructor();

        int modifiers = constructor.getModifiers();

        assertEquals(Modifier.PRIVATE, modifiers);

        RentalSystem instance = RentalSystem.getInstance_vai();
        assertNotNull(instance);
    }
}