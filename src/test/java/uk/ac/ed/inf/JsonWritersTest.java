package uk.ac.ed.inf;

import org.junit.Test;
import static org.junit.Assert.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JsonWritersTest {

    // Test for deliveriesJson method
    @Test
    public void testDeliveriesJson() {
        // Create mock orders
        Order order1 = createMockOrder(OrderStatus.DELIVERED, OrderValidationCode.NO_ERROR, 1000);
        Order order2 = createMockOrder(OrderStatus.VALID_BUT_NOT_DELIVERED, OrderValidationCode.TOTAL_INCORRECT, 2000);

        List<Order> orders = Arrays.asList(order1, order2);

        // Generate the JSON
        JsonArray result = JsonWriters.deliveriesJson(orders);

        // Validate JSON structure
        assertEquals(2, result.size());

        JsonObject orderJson1 = result.get(0).getAsJsonObject();
        assertEquals("DELIVERED", orderJson1.get("orderStatus").getAsString());
        assertEquals("NO_ERROR", orderJson1.get("orderValidationCode").getAsString());
        assertEquals(1000, orderJson1.get("costInPence").getAsInt());

        JsonObject orderJson2 = result.get(1).getAsJsonObject();
        assertEquals("VALID_BUT_NOT_DELIVERED", orderJson2.get("orderStatus").getAsString());
        assertEquals("TOTAL_INCORRECT", orderJson2.get("orderValidationCode").getAsString());
        assertEquals(2000, orderJson2.get("costInPence").getAsInt());
    }

    // Test for flightPathJson method
    @Test
    public void testFlightPathJson() {
        // Create mock flight moves and order numbers
        FlightMove move1 = createMockFlightMove(new LngLat(-3.186874, 55.944494), new LngLat(-3.185, 55.944));
        FlightMove move2 = createMockFlightMove(new LngLat(-3.185, 55.944), new LngLat(-3.184, 55.945));

        FlightMove[][] flightPath = {{move1, move2}};
        List<String> orderNumbers = Collections.singletonList("1");

        // Generate the JSON
        JsonArray result = JsonWriters.flightPathJson(flightPath, orderNumbers);

        // Validate JSON structure
        assertEquals(5, result.size());

        JsonObject moveJson1 = result.get(0).getAsJsonObject();
        assertEquals("1", moveJson1.get("orderNo").getAsString());
        assertEquals(-3.186874, moveJson1.get("fromLongitude").getAsDouble(), 0.0001);
        assertEquals(55.944494, moveJson1.get("fromLatitude").getAsDouble(), 0.0001);
        assertEquals(-3.185, moveJson1.get("toLongitude").getAsDouble(), 0.0001);
        assertEquals(55.944, moveJson1.get("toLatitude").getAsDouble(), 0.0001);

        JsonObject hoverRestaurantJson = result.get(2).getAsJsonObject();
        assertEquals("1", hoverRestaurantJson.get("orderNo").getAsString());
        assertEquals(999, hoverRestaurantJson.get("angle").getAsInt());
    }

    // Test for dronePathGeoJson method
    @Test
    public void testDronePathGeoJson() {
        // Create mock flight moves
        FlightMove move1 = createMockFlightMove(new LngLat(-3.186874, 55.944494), new LngLat(-3.185, 55.944));
        FlightMove move2 = createMockFlightMove(new LngLat(-3.185, 55.944), new LngLat(-3.184, 55.945));

        FlightMove[][] flightPath = {{move1, move2}};

        // Generate the GeoJSON
        JsonObject result = JsonWriters.dronePathGeoJson(flightPath);

        // Validate GeoJSON structure
        assertTrue(result.has("type"));
        assertEquals("FeatureCollection", result.get("type").getAsString());

        JsonArray features = result.getAsJsonArray("features");
        assertEquals(1, features.size());

        JsonObject feature = features.get(0).getAsJsonObject();
        JsonObject geometry = feature.getAsJsonObject("geometry");
        JsonArray coordinates = geometry.getAsJsonArray("coordinates");

        assertTrue(!coordinates.isEmpty()); // Ensure that coordinates are present
    }

    // Helper method to create a mock order
    private Order createMockOrder(OrderStatus status, OrderValidationCode validationCode, int cost) {
        Order order = new Order();
        order.setOrderStatus(status);
        order.setOrderValidationCode(validationCode);
        order.setPriceTotalInPence(cost);
        return order;
    }

    // Helper method to create a mock flight move
    private FlightMove createMockFlightMove(LngLat from, LngLat to) {
        return new FlightMove(from, 45, to); // Arbitrary angle of 45 degrees
    }
}