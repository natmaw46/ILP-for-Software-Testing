package uk.ac.ed.inf;

import org.junit.Test;
import static org.junit.Assert.*;

import junit.framework.TestCase;
import uk.ac.ed.inf.RestServiceReader;
import uk.ac.ed.inf.ilp.data.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;

public class RestServiceReaderTest extends TestCase {

    // 1. Test valid URL (already implemented)
    public void testValidServerConnection() {
        String urlString = "https://ilp-rest-2024.azurewebsites.net/";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            assertEquals(200, responseCode); // Check if the response code is 200 (OK)
        } catch (Exception e) {
            fail("Failed to connect to the server: " + urlString);
        }
    }

    // 2. Test invalid URL (non-existent website)
    public void testInvalidServerConnection() throws Exception {
        String urlString = "https://thisisnotarealwebsite.com/";
        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
    }

    // 3. Test malformed URL
    public void testMalformedUrl() {
        String urlString = "htp://malformed-url.com/";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            fail("Expected MalformedURLException to be thrown");
        } catch (MalformedURLException e) {
            // Test passes since the exception is expected
            assertEquals("unknown protocol: htp", e.getMessage());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getClass().getName());
        }
    }

    // 4. Test URL that returns 404 error (non-existent endpoint)
    public void testInvalidEndpoint() {
        String urlString = "https://ilp-rest-2024.azurewebsites.net/invalidendpoint";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            assertEquals(404, responseCode); // Check if the response code is 404 (Not Found)
        } catch (Exception e) {
            fail("Failed to connect to the server: " + urlString);
        }
    }

    // 5. Test timeout (simulate slow connection)
    public void testTimeoutServerConnection() {
        String urlString = "https://ilp-rest-2024.azurewebsites.net/";
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(500); // Timeout 500ms
            connection.setReadTimeout(500);    // Timeout 500ms
            connection.connect();
            int responseCode = connection.getResponseCode();
            assertTrue(responseCode >= 200 && responseCode < 400); // Ensure response code is in the success range
        } catch (Exception e) {
            fail("Connection timed out or failed for: " + urlString);
        }
    }

    // 6. Test getting restaurant data and print it out
    public void testValidRestaurantData() {
        String urlString = "https://ilp-rest-2024.azurewebsites.net/";
        Restaurant[] rest = RestServiceReader.getRestaurant(urlString);
        assertNotNull("Restaurants should not be null", rest);
        assertTrue("Restaurants should contain at least one restaurant", rest.length > 0);

        for (Restaurant restaurant : rest) {
            System.out.println(restaurant.name());
            System.out.println(restaurant.location());
            System.out.print("Opening Days: [");
            for (DayOfWeek day : restaurant.openingDays()) {
                System.out.print(day.toString() + ", ");
            }
            System.out.println("]");
            for (Pizza pizza : restaurant.menu()) {
                System.out.print(pizza.name() + ": ");
                System.out.println(pizza.priceInPence());
            }
            System.out.println();
        }
    }

    // 7. Test getting orders
    public void testValidOrderData() {
        String urlString = "https://ilp-rest-2024.azurewebsites.net/";
        LocalDate date = LocalDate.of(2023, 11, 15);
        Order[] ord = RestServiceReader.getOrder(urlString, date);
        assertNotNull("Orders should not be null", ord);
        assertTrue("Orders should contain at least one order", ord.length > 0);

        for (Order order : ord) {
            System.out.println(order.getOrderNo());
            System.out.println(order.getOrderDate());
            System.out.println(order.getOrderStatus());
            System.out.println(order.getOrderValidationCode());
            System.out.println(order.getPriceTotalInPence());
            for (Pizza pizza : order.getPizzasInOrder()) {
                System.out.print(pizza.name() + ": ");
                System.out.println(pizza.priceInPence());
            }
            System.out.println();
        }
    }

    // 8. Test getting central area data
    public void testValidCentralAreaData() {
        String urlString = "https://ilp-rest-2024.azurewebsites.net/";
        NamedRegion centralArea = RestServiceReader.getCentralArea(urlString);
        assertNotNull("Central area should not be null", centralArea);

        System.out.println("Central Area: " + centralArea.name());
        for (LngLat point : centralArea.vertices()) {
            System.out.print("[" + point.lat() + ", ");
            System.out.println(point.lng() + "]");
        }
        System.out.println();
    }

    // 9. Test getting no-fly zones data
    public void testValidNoFlyZonesData() {
        String urlString = "https://ilp-rest-2024.azurewebsites.net/";
        NamedRegion[] noFlyZones = RestServiceReader.getNoFlyZones(urlString);
        assertNotNull("No-fly zones should not be null", noFlyZones);
        assertTrue("No-fly zones should contain at least one region", noFlyZones.length > 0);

        for (NamedRegion region : noFlyZones) {
            System.out.println(region.name());
            for (LngLat point : region.vertices()) {
                System.out.print("[" + point.lat() + ", ");
                System.out.println(point.lng() + "]");
            }
            System.out.println();
        }
    }
}

