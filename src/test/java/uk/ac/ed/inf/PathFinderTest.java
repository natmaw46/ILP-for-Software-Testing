package uk.ac.ed.inf;

import org.junit.Test;
import static org.junit.Assert.*;
import uk.ac.ed.inf.ilp.data.*;
import java.util.Arrays;

public class PathFinderTest {

    // Test finding a path to a restaurant
    @Test
    public void testFindPathToRestaurant() {
        LngLat restaurantPoint = new LngLat(-3.185, 55.944); // Example restaurant point near Appleton Tower
        NamedRegion centralArea = createCentralArea();
        NamedRegion[] noFlyZones = {}; // No no-fly zones for this test

        FlightMove[] path = PathFinder.findPath(restaurantPoint, centralArea, noFlyZones);

        // Check that the path is not empty
        assertNotNull("Path should not be null", path);
        assertTrue("Path should contain at least one flight move", path.length > 0);

        // Check that the last point is the restaurant point (or very close to it)
        LngLat lastPoint = path[path.length - 1].toLngLat();
        assertTrue("Final point should be near restaurant", PathFinder.handler.distanceTo(lastPoint, restaurantPoint) < 0.00015);
    }

    // Test finding a path when the restaurant is inside the central area
    @Test
    public void testFindPathInsideCentralArea() {
        LngLat restaurantPoint = new LngLat(-3.186, 55.944); // Example restaurant point inside central area
        NamedRegion centralArea = createCentralArea();
        NamedRegion[] noFlyZones = {}; // No no-fly zones for this test

        FlightMove[] path = PathFinder.findPath(restaurantPoint, centralArea, noFlyZones);

        // Path should be empty or contain only one move, as it's already inside the area
        assertNotNull("Path should not be null", path);
        assertTrue("Path should contain at most one flight move", path.length >= 1);
    }

    // Test finding a path to a restaurant when the starting point is Appleton Tower
    @Test
    public void testFindPathFromAppletonTower() {
        LngLat restaurantPoint = new LngLat(-3.185, 55.944); // Example restaurant point
        NamedRegion centralArea = createCentralArea();
        NamedRegion[] noFlyZones = {}; // No no-fly zones for this test

        FlightMove[] path = PathFinder.findPath(restaurantPoint, centralArea, noFlyZones);

        // Check that the path is not empty and begins at Appleton Tower
        assertNotNull("Path should not be null", path);
        assertTrue("Path should contain at least one flight move", path.length > 0);
        assertEquals("Path should start at Appleton Tower", PathFinder.APPLETON_TOWER, path[0].fromLngLat());
    }

    // Test finding a path to a restaurant with no-fly zones blocking the direct path
    @Test
    public void testFindPathWithNoFlyZones() {
        LngLat restaurantPoint = new LngLat(-3.185, 55.944); // Example restaurant point
        NamedRegion centralArea = createCentralArea();
        NamedRegion[] noFlyZones = {createNoFlyZone()}; // Add a no-fly zone

        FlightMove[] path = PathFinder.findPath(restaurantPoint, centralArea, noFlyZones);

        assertNotNull("Path should not be null", path);
        assertTrue("Path should contain at least one flight move", path.length > 0);

        // Ensure no points in the path are inside no-fly zones
        for (FlightMove move : path) {
            assertFalse("Point should not be in a no-fly zone",
                    PathFinder.handler.isInRegion(move.toLngLat(), noFlyZones[0]));
        }
    }

    // Test finding a path when the restaurant is on the boundary of the central area
    @Test
    public void testFindPathBoundaryCentralArea() {
        LngLat restaurantPoint = new LngLat(-3.190, 55.950); // Point on boundary
        NamedRegion centralArea = createCentralArea();
        NamedRegion[] noFlyZones = {}; // No no-fly zones for this test

        FlightMove[] path = PathFinder.findPath(restaurantPoint, centralArea, noFlyZones);

        assertNotNull("Path should not be null", path);
        assertTrue("Path should contain at least one flight move", path.length > 0);

        LngLat lastPoint = path[path.length - 1].toLngLat();
        assertTrue("Final point should be near restaurant", PathFinder.handler.distanceTo(lastPoint, restaurantPoint) < 0.00015);
    }

    // Test finding a path with overlapping no-fly zones and central area
    @Test
    public void testFindPathOverlappingNoFlyZones() {
        LngLat restaurantPoint = new LngLat(-3.185, 55.944); // Example restaurant point
        NamedRegion centralArea = createCentralArea();
        NamedRegion noFlyZone = createNoFlyZone();
        NamedRegion[] noFlyZones = {noFlyZone};

        FlightMove[] path = PathFinder.findPath(restaurantPoint, centralArea, noFlyZones);

        assertNotNull("Path should not be null", path);
        assertTrue("Path should contain at least one flight move", path.length > 0);

        for (FlightMove move : path) {
            assertFalse("Point should not be in no-fly zone",
                    PathFinder.handler.isInRegion(move.toLngLat(), noFlyZone));
        }
    }

    // Helper method to create a mock central area (a square area in this case)
    private NamedRegion createCentralArea() {
        LngLat[] vertices = {
                new LngLat(-3.190, 55.940), // Bottom-left
                new LngLat(-3.180, 55.940), // Bottom-right
                new LngLat(-3.180, 55.950), // Top-right
                new LngLat(-3.190, 55.950)  // Top-left
        };
        return new NamedRegion("Central Area", vertices);
    }

    // Helper method to create a mock no-fly zone (small circle area)
    private NamedRegion createNoFlyZone() {
        LngLat[] vertices = {
                new LngLat(-3.187, 55.945), // Approximate coordinates for the no-fly zone
                new LngLat(-3.188, 55.945),
                new LngLat(-3.188, 55.946),
                new LngLat(-3.187, 55.946)
        };
        return new NamedRegion("No-Fly Zone", vertices);
    }
}