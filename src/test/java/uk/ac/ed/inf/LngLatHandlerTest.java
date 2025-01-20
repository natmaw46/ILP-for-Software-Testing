package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

public class LngLatHandlerTest extends TestCase {
    final private LngLatHandler handler = new LngLatHandler();

    public void testDistanceTo() {
        final LngLat pointA = new LngLat(3, 2);
        final LngLat pointB = new LngLat(4, 1);

        // If two points are the same, the distance should be 0.
        assertEquals(0.0, handler.distanceTo(pointA, pointA), 1E-15);

        final double expectedDistance = Math.sqrt(2);

        // Check that the distance is the same regardless of the order of the points.
        assertEquals(expectedDistance, handler.distanceTo(pointA, pointB), 1E-15);
        assertEquals(expectedDistance, handler.distanceTo(pointB, pointA), 1E-15);
    }

    public void testIsCloseTo() {
        final LngLat startPosition = new LngLat(0.0, 0.0);

        // The two points are close to each other.
        assertTrue(handler.isCloseTo(startPosition, new LngLat(0.0001, 0.0001)));

        // The two points are not close to each other.
        assertFalse(handler.isCloseTo(startPosition, new LngLat(0.001, 0.001)));
    }

    public void testIsInCentralArea() {
        // This unit also indirectly tests `isInRegion` on a rectangle (polygon with 4 vertices).
        final NamedRegion centralArea = new NamedRegion("central", new LngLat[]{
                new LngLat(-3.192473, 55.946233),
                new LngLat(-3.192473, 55.942617),
                new LngLat(-3.184319, 55.942617),
                new LngLat(-3.184319, 55.946233),
        });

        // The point is within the central area.
        assertTrue(handler.isInRegion(new LngLat(-3.188396, 55.944), centralArea));

        // The point is not within the central area.
        assertFalse(handler.isInRegion(new LngLat(-3.2, 55.944), centralArea));
    }

    public void testIsInRegion() {
        // Establishes a closed polygon with 11 vertices.
        final NamedRegion polygon = new NamedRegion("Polygon", new LngLat[]{
                new LngLat(0, 20),    // A
                new LngLat(-10, 20),  // B
                new LngLat(-10, 5),   // C
                new LngLat(-20, 10),  // D
                new LngLat(-30, 0),   // E
                new LngLat(-5, -5),   // F
                new LngLat(-20, -25), // G
                new LngLat(0, -20),   // H
                new LngLat(20, -20),  // I
                new LngLat(0, 0),     // J
                new LngLat(10, 10),   // K
        });

        // Points inside the polygon.
        assertTrue(handler.isInRegion(new LngLat(0, 10), polygon));
        assertTrue(handler.isInRegion(new LngLat(7, 12.99), polygon));
        assertTrue(handler.isInRegion(new LngLat(-28, 1), polygon));

        // Points outside the polygon.
        assertFalse(handler.isInRegion(new LngLat(5, 0), polygon));
        assertFalse(handler.isInRegion(new LngLat(-5, -23), polygon));
    }

    public void testNextPosition() {
        final double tolerance = 1E-15;

        double[][] cases = new double[][]{
                // [indexing]
                // 0: expected longitude
                // 1: expected latitude
                // 2: functional angle (in degrees)
                new double[]{1.0606601717798212E-4, 1.0606601717798212E-4, 45},
                new double[]{1.5000000000000001E-4, 0, 0},
        };

        for (double[] values : cases) {
            final LngLat nextPoint = handler.nextPosition(new LngLat(0, 0), values[2]);

            assertEquals(values[0], nextPoint.lng(), tolerance);
            assertEquals(values[1], nextPoint.lat(), tolerance);
        }
    }
}

