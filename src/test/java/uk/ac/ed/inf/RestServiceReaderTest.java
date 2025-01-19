package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class RestServiceReaderTest {

    public static void main( String[] args ) {

        //Get Restaurants from Rest
        Restaurant[] rest = RestServiceReader.getRestaurant("https://ilp-rest.azurewebsites.net/");
        for (Restaurant restaurant : rest) {
            System.out.println(restaurant.name());
            System.out.println(restaurant.location());
            System.out.print("[");
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


        //Get Orders from Rest
        Order[] ord = RestServiceReader.getOrder("https://ilp-rest.azurewebsites.net", LocalDate.ofEpochDay(2023-11-15));
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


        //Get Central Area from Rest
        NamedRegion centralArea = RestServiceReader.getCentralArea("https://ilp-rest.azurewebsites.net");
        System.out.println(centralArea.name());
        for (LngLat point : centralArea.vertices()) {
            System.out.print("[" + point.lat() + ", ");
            System.out.println(point.lng() + "]");
        }
        System.out.println();


        //Get No-Fly Zones from Rest
        NamedRegion[] noFlyZones = RestServiceReader.getNoFlyZones("https://ilp-rest.azurewebsites.net");
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
