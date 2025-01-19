package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.data.Order;

import java.util.List;

public class JsonWriters {

    //Storing Orders into Json File Format
    public static JsonArray deliveriesJson(List<Order> ordersAfterValidation) {

        JsonArray validatedOrder = new JsonArray();

        for (Order order : ordersAfterValidation) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("orderNo", order.getOrderNo());
            jsonObject.addProperty("orderStatus", order.getOrderStatus().toString());
            jsonObject.addProperty("orderValidationCode", order.getOrderValidationCode().toString());
            jsonObject.addProperty("costInPence", order.getPriceTotalInPence());

            validatedOrder.add(jsonObject);
        }

        return validatedOrder;
    }


    //Storing FLight Path into Json File Format
    public static JsonArray flightPathJson(FlightMove[][] allFlightPath, List<String> orderNumberList) {

        JsonArray FlightPath = new JsonArray();
        int iterate = 0;

        for (FlightMove[] singleFlightPath : allFlightPath) {

            FlightMove hoverFlight = null;

            for (FlightMove singleMove : singleFlightPath) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderNo", orderNumberList.get(iterate));
                jsonObject.addProperty("fromLongitude", singleMove.fromLngLat().lng());
                jsonObject.addProperty("fromLatitude", singleMove.fromLngLat().lat());
                jsonObject.addProperty("angle", singleMove.angle());
                jsonObject.addProperty("toLongitude", singleMove.toLngLat().lng());
                jsonObject.addProperty("toLatitude", singleMove.toLngLat().lat());

                hoverFlight = singleMove;

                FlightPath.add(jsonObject);
            }

            //Insert Hover Move
            JsonObject hoverRestaurant = new JsonObject();
            assert hoverFlight != null;
            hoverRestaurant.addProperty("orderNo", orderNumberList.get(iterate));
            hoverRestaurant.addProperty("fromLongitude", hoverFlight.toLngLat().lng());
            hoverRestaurant.addProperty("fromLatitude", hoverFlight.toLngLat().lat());
            hoverRestaurant.addProperty("angle", 999);
            hoverRestaurant.addProperty("toLongitude", hoverFlight.toLngLat().lng());
            hoverRestaurant.addProperty("toLatitude", hoverFlight.toLngLat().lat());

            FlightPath.add(hoverRestaurant);

            for ( int i = singleFlightPath.length - 1; i > 0; i-- ) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("orderNo", orderNumberList.get(iterate));
                jsonObject.addProperty("fromLongitude", singleFlightPath[i].toLngLat().lng());
                jsonObject.addProperty("fromLatitude",  singleFlightPath[i].toLngLat().lat());
                jsonObject.addProperty("angle",  (singleFlightPath[i].angle()+180)%360);
                jsonObject.addProperty("toLongitude",  singleFlightPath[i].fromLngLat().lng());
                jsonObject.addProperty("toLatitude",  singleFlightPath[i].fromLngLat().lat());

                FlightPath.add(jsonObject);
            }

            JsonObject hoverAT = new JsonObject();
            hoverAT.addProperty("orderNo", orderNumberList.get(iterate));
            hoverAT.addProperty("fromLongitude", PathFinder.APPLETON_TOWER.lng());
            hoverAT.addProperty("fromLatitude", PathFinder.APPLETON_TOWER.lat());
            hoverAT.addProperty("angle", 999);
            hoverAT.addProperty("toLongitude", PathFinder.APPLETON_TOWER.lng());
            hoverAT.addProperty("toLatitude", PathFinder.APPLETON_TOWER.lat());

            FlightPath.add(hoverAT);

            iterate += 1;

        }


        return FlightPath;

    }


    //Storing Drone LngLat into GeoJson File Format
    public static JsonObject dronePathGeoJson(FlightMove[][] allFlightPath) {

        JsonObject flightPath = new JsonObject();
        JsonArray featuresArray = new JsonArray();
        JsonObject emptyProperties = new JsonObject();
        JsonObject features = new JsonObject();
        JsonObject geometry = new JsonObject();


        JsonArray coordinates = getJsonElements(allFlightPath);

        geometry.add("coordinates", coordinates);
        geometry.addProperty("type", "LineString");

        features.addProperty("type", "Feature");
        features.add("properties", emptyProperties);
        features.add("geometry", geometry);

        featuresArray.add(features);
        flightPath.addProperty("type", "FeatureCollection");
        flightPath.add("features", featuresArray);

        return flightPath;

    }

    private static JsonArray getJsonElements(FlightMove[][] allFlightPath) {
        JsonArray coordinates = new JsonArray();

        JsonArray tempAT = new JsonArray();

        tempAT.add(PathFinder.APPLETON_TOWER.lng());
        tempAT.add(PathFinder.APPLETON_TOWER.lat());

        coordinates.add(tempAT);

        for (FlightMove[] singleFlightPath : allFlightPath) {

            for (FlightMove singleMove : singleFlightPath) {

                JsonArray temp = new JsonArray();

                temp.add(singleMove.toLngLat().lng());
                temp.add(singleMove.toLngLat().lat());

                coordinates.add(temp);

            }

            for ( int i = singleFlightPath.length - 1; i > 0; i-- ) {

                JsonArray temp = new JsonArray();

                temp.add(singleFlightPath[i].fromLngLat().lng());
                temp.add(singleFlightPath[i].fromLngLat().lat());

                coordinates.add(temp);

            }

        }
        return coordinates;
    }
}
