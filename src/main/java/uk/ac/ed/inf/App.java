package uk.ac.ed.inf;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.ArrayUtils;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


public class App 
{
    public static void main( String[] args )
    {

        //Collect Arguments
        LocalDate orderDate = null;
        String baseURL = null;

        if (args.length != 2) {
            System.out.println("Usage: App 'YYYY-MM-DD' rest_service_URL");
            System.exit(1);

        } else {
            try {
                orderDate = LocalDate.parse(args[0]);
                baseURL = args[1];

            } catch (DateTimeParseException e) {
                throw new RuntimeException("Date parsing failed", e);
            }
        }
        System.out.println("Order Date on: " + orderDate);
        System.out.println("Rest Service URL: " + baseURL);


        //Start Validating Orders
        List<Order> ordersAfterValidation = new ArrayList<>();
        Restaurant[] restaurants = RestServiceReader.getRestaurant(baseURL);
        Order[] allOrders = RestServiceReader.getOrder(baseURL, orderDate);

        for (Order singleOrder : allOrders) {

            OrderValidator validator = new OrderValidator();
            Order newOrder = validator.validateOrder(singleOrder, restaurants);

            ordersAfterValidation.add(newOrder);
        }
        System.out.println("Validated All Orders");


        //Create a filtered out list for valid orders but still not delivered
        Order[] validOrdersOnly = ordersAfterValidation.stream().filter(order ->
                order.getOrderStatus().equals(OrderStatus.VALID_BUT_NOT_DELIVERED)).toArray(Order[]::new);


        //Create flight path for orders ready for delivery
        NamedRegion centralArea = RestServiceReader.getCentralArea(baseURL);
        NamedRegion[] noFlyZones = RestServiceReader.getNoFlyZones(baseURL);
        FlightMove[][] allFlightPath = new FlightMove[0][];
        List<String> orderNumberList =  new ArrayList<>();

        for (Order order : validOrdersOnly) {
            LngLat restaurantLocation = findingRestaurant(order.getPizzasInOrder()[0].name(), restaurants);

            orderNumberList.add(order.getOrderNo());
            FlightMove[] singleFlightPath = PathFinder.findPath(restaurantLocation, centralArea, noFlyZones);
            allFlightPath = ArrayUtils.add(allFlightPath, singleFlightPath);

            for (Order single : ordersAfterValidation) {
                if (single.getOrderNo().equals(order.getOrderNo())) {
                    single.setOrderStatus(OrderStatus.DELIVERED);

                }
            }
        }
        System.out.println("Flight Path Generated");


        //create resultFiles folder path
        File folder = new File("resultfiles");
        if (!folder.exists()) {
            boolean folderCreated = folder.mkdirs();
            if (!folderCreated) {
                System.out.println("Failed to create the folder.");
                return;
            }
        }
        String folderPath = "resultfiles" + File.separator;


        //Create Json File for All Orders
        JsonArray validatedOrder = JsonWriters.deliveriesJson(ordersAfterValidation);
        try (FileWriter file = new FileWriter(folderPath + "deliveries-YEAR-MM-DD.json")) {

            file.write(validatedOrder.toString());
            file.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Create Json File for the FlightPath
        JsonArray flightPathJson = JsonWriters.flightPathJson(allFlightPath, orderNumberList);
        try (FileWriter file = new FileWriter(folderPath + "flightpath-YYYY-MM-DD.json")) {

            file.write(flightPathJson.toString());
            file.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        //Create GEOJson File for Drone Path
        JsonObject dronePathGeoJson = JsonWriters.dronePathGeoJson(allFlightPath);
        try (FileWriter file = new FileWriter(folderPath + "drone-YYYY-MM-DD.geojson")) {

            file.write(dronePathGeoJson.toString());
            file.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("All Json and GeoJson Files were Created in Folder resultfiles");

    }

    public static LngLat findingRestaurant(String pizzaName, Restaurant[] restaurants) {
        for (Restaurant restaurant : restaurants) {
            for (Pizza pizza : restaurant.menu()) {
                if (pizza.name().equals(pizzaName)) {
                    return restaurant.location();
                }
            }
        }
        return null;
    }
}
