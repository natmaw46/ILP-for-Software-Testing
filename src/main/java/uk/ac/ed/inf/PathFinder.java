package uk.ac.ed.inf;

import org.apache.commons.lang3.ArrayUtils;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

public class PathFinder {

    //Appleton Tower LngLat
    public static final LngLat APPLETON_TOWER = new LngLat(-3.186874, 55.944494);

    //All 16 Angles
    public static final double[] allPossibleAngles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};

    static LngLatHandler handler = new LngLatHandler();


    //Star Creating Path
    public static FlightMove[] findPath(LngLat restaurantPoint, NamedRegion centralArea, NamedRegion[] noFlyZones) {

        LngLat endPoint;

        //Set Closest point in Central Area to Restaurant
        if (!handler.isInRegion(restaurantPoint, centralArea)) {
            endPoint = findClosetPointInRegion(restaurantPoint, centralArea);
        } else {
            endPoint = restaurantPoint;
        }

        FlightMove[] path = new FlightMove[0];
        LngLat currentPoint = APPLETON_TOWER;
        LngLat parentPoint = APPLETON_TOWER;

        //Loop when Current Point to at Restaurant/Closest Point in CA
        while (handler.distanceTo(currentPoint, endPoint) > 0.00015) {

            LngLat newClosest = null;
            double closestAngle = 0;


            //Find Best Next Step for all 16 Angles
            for (double angle : allPossibleAngles) {
                LngLat newNode = handler.nextPosition(currentPoint, angle);
                double newDistanceToEnd = handler.distanceTo(newNode, endPoint);

                if (newClosest == null) {
                    newClosest = newNode;

                } else if (newDistanceToEnd < handler.distanceTo(newClosest, endPoint) &&
                        !pointIsInNoFly(newNode, noFlyZones) &&
                        !newNode.equals(parentPoint)) {
                    newClosest = newNode;
                    closestAngle = angle;
                }
            }

            FlightMove newFlight = new FlightMove(currentPoint, closestAngle , newClosest);

            path = ArrayUtils.add(path, newFlight);
            parentPoint = currentPoint;
            currentPoint = newClosest;

        }


        //Finish Path from Closest point in Central Area to Restaurant
        if (handler.distanceTo(currentPoint, restaurantPoint) > 0.00015) {
            while (path.length > 0 && handler.distanceTo(currentPoint, restaurantPoint) > 0.00015) {
                LngLat newClosest = null;
                double closestAngle = 0;

                for (double angle : allPossibleAngles) {
                    LngLat newNode = handler.nextPosition(currentPoint, angle);
                    double newDistanceToEnd = handler.distanceTo(newNode, restaurantPoint);

                    if (newClosest == null) {
                        newClosest = newNode;

                    } else if (newDistanceToEnd < handler.distanceTo(newClosest, restaurantPoint)) {
                        newClosest = newNode;
                        closestAngle = angle;
                    }
                }

                FlightMove newFlight = new FlightMove(currentPoint, closestAngle , newClosest);

                path = ArrayUtils.add(path, newFlight);
                currentPoint = newClosest;
            }
        }

        return path;
    }

    private static boolean pointIsInNoFly(LngLat current, NamedRegion[] noFlyZones) {
        for (NamedRegion noFlyZone : noFlyZones) {
            if (handler.isInRegion(current, noFlyZone)) {
                return true;
            }
        }
        return false;
    }

    private static LngLat findClosetPointInRegion(LngLat restaurant, NamedRegion centralArea) {

        double restaurantX = restaurant.lng();
        double restaurantY = restaurant.lat();

        double cAreaMinX = centralArea.vertices()[0].lng();
        double cAreaMinY = centralArea.vertices()[2].lat();

        double cAreaMaxX = centralArea.vertices()[2].lng();
        double cAreaMaxY = centralArea.vertices()[0].lat();

        double newPointX = Math.min(Math.max(restaurantX, cAreaMinX), cAreaMaxX);
        double newPointY = Math.min(Math.max(restaurantY, cAreaMinY), cAreaMaxY);

        return new LngLat(newPointX, newPointY);
    }

}
