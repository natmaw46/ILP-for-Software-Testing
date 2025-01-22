package uk.ac.ed.inf;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Unit test for the App class.
 */
public class AppTest {

    LocalDate startDate = LocalDate.of(2023, 9, 1);
    LocalDate endDate = LocalDate.of(2024, 1, 28);
    Set<LocalDate> randomDates = new HashSet<>();
    String projectDir = System.getProperty("user.dir") + "/resultfiles/";

    @Before
    public void setup() {
        // Generate a set of random dates for testing
        while (randomDates.size() < 3) {
            LocalDate date = generateRandomDate(startDate, endDate);
            randomDates.add(date);
        }
    }

    @Test
    public void testFileGeneration() {
        for (LocalDate date : randomDates) {
            String dateStr = date.toString();

            App.main(new String[]{dateStr, "https://ilp-rest-2024.azurewebsites.net"});

            String flightPathFileName = projectDir + "flightpath-" + dateStr + ".json";
            String droneFileName = projectDir + "drone-" + dateStr + ".geojson";
            String deliveriesFileName = projectDir + "deliveries-" + dateStr + ".json";

            assertTrue("Flight path file for " + dateStr + " not created", Files.exists(Paths.get(flightPathFileName)));
            assertTrue("Drone file for " + dateStr + " not created", Files.exists(Paths.get(droneFileName)));
            assertTrue("Deliveries file for " + dateStr + " not created", Files.exists(Paths.get(deliveriesFileName)));
        }
    }

    @Test
    public void testFindingRestaurant() {
        String baseURL = "https://ilp-rest-2024.azurewebsites.net";
        Restaurant[] restaurants = RestServiceReader.getRestaurant(baseURL);

        String pizzaName = restaurants[0].menu()[0].name();
        LngLat location = App.findingRestaurant(pizzaName, restaurants);
        assertNotNull("Expected location to be found for a valid pizza name", location);

        LngLat nullLocation = App.findingRestaurant("NonExistingPizza", restaurants);
        assertNull("Expected null location for a non-existing pizza name", nullLocation);
    }

    @Test
    public void testEmptyOrderProcessing() {
        LocalDate date = LocalDate.of(2025, 1, 1);
        String baseURL = "https://ilp-rest-2024.azurewebsites.net";

        App.main(new String[]{date.toString(), baseURL});

        String flightPathFileName = projectDir + "flightpath-" + date + ".json";
        String droneFileName = projectDir + "drone-" + date + ".geojson";
        String deliveriesFileName = projectDir + "deliveries-" + date + ".json";

        assertTrue("Flight path file for empty orders should be created", Files.exists(Paths.get(flightPathFileName)));
        assertTrue("Drone file for empty orders should be created", Files.exists(Paths.get(droneFileName)));
        assertTrue("Deliveries file for empty orders should be created", Files.exists(Paths.get(deliveriesFileName)));
    }

    @Test
    public void testFolderCreationFailure() {
        String originalProjectDir = System.getProperty("user.dir");
        System.setProperty("user.dir", "/root/");

        try {
            App.main(new String[]{"2024-01-01", "https://ilp-rest-2024.azurewebsites.net"});
            fail("Expected RuntimeException due to folder creation failure");
        } catch (RuntimeException e) {
            assertTrue(true);
        } finally {
            System.setProperty("user.dir", originalProjectDir);
        }
    }

    @After
    public void cleanup() throws IOException {
        for (LocalDate date : randomDates) {
            String flightPathFileName = projectDir + "flightpath-" + date + ".json";
            String droneFileName = projectDir + "drone-" + date + ".geojson";
            String deliveriesFileName = projectDir + "deliveries-" + date + ".json";

            Files.deleteIfExists(Paths.get(flightPathFileName));
            Files.deleteIfExists(Paths.get(droneFileName));
            Files.deleteIfExists(Paths.get(deliveriesFileName));

            System.out.println("Files for " + date + " were successfully deleted after testing.");
        }
    }

    private LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
        Random random = new Random();
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        long randomEpochDay = startEpochDay + random.nextInt((int) (endEpochDay - startEpochDay));
        return LocalDate.ofEpochDay(randomEpochDay);
    }
}