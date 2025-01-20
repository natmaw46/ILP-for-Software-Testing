//package uk.ac.ed.inf;
//
//public class backuptest {


//package uk.ac.ed.inf;
//
//
//import uk.ac.ed.inf.ilp.data.LngLat;
//import uk.ac.ed.inf.ilp.data.NamedRegion;
//
//public class LngLatHandlerTest {
//    public static void main( String[] args ) {
//        LngLat startPosition = new LngLat(0.0000, 0.0000);
//        LngLat endPosition = new LngLat(0.0001, 0.0000);
//
//        NamedRegion region = new NamedRegion("exampleRegion",
//                new LngLat[]{
//                        new LngLat(0.0000,0.0000)
//                        ,new LngLat(0.0001, 0.0000)
//                        ,new LngLat(0.0001,0.0001)
//                        ,new LngLat(0.0000,0.0001)});
//
//        LngLatHandler lngLatHandler = new LngLatHandler();
//        double distance = lngLatHandler.distanceTo(startPosition,endPosition);
//        boolean isCloseTo = lngLatHandler.isCloseTo(startPosition,endPosition);
//        LngLat nextPosition = lngLatHandler.nextPosition(startPosition, 180);
//        boolean isInRegion = lngLatHandler.isInRegion(new LngLat(0.00005,0.00005), region);
//
//        System.out.println(distance);
//        System.out.println(isCloseTo);
//        System.out.println(nextPosition);
//        System.out.println(isInRegion);
//    }
//}


//package uk.ac.ed.inf;
//
//import org.junit.Test;
//import uk.ac.ed.inf.ilp.data.CreditCardInformation;
//import uk.ac.ed.inf.ilp.constant.OrderStatus;
//import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
//import uk.ac.ed.inf.ilp.data.LngLat;
//import uk.ac.ed.inf.ilp.data.Order;
//import uk.ac.ed.inf.ilp.data.Pizza;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//import java.util.Random;
//
//import uk.ac.ed.inf.ilp.data.Restaurant;
//
//import static org.junit.Assert.*;
//
//public class OrderValidatorTest
//{
//    @Test
//    public static void main( String[] args )
//    {
//        CreditCardInformation creditCardSample = new CreditCardInformation();
//
//        creditCardSample.setCvv("952");
//        creditCardSample.setCreditCardNumber("5555555555554444");
//        creditCardSample.setCreditCardExpiry("06/38");
//
//
//        LocalDate date = LocalDate.of(2023,9,1);
//
//        Pizza[] pizzas = { new Pizza("Super Cheese", 1400), new Pizza("All Shrooms", 900)};
//
//        Restaurant[] restaurants = new Restaurant[4];
//        restaurants[0] = new Restaurant("Civerinos Slice"
//                ,new LngLat(-3.1912869215011597,55.945535152517735)
//                ,new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY}
//                ,new Pizza[]{new Pizza("Margarita",1000), new Pizza("Calzone", 1400)});
//
//        restaurants[1] = new Restaurant("Sora Lella Vegan Restaurant"
//                ,new LngLat(-3.202541470527649,55.943284737579376)
//                ,new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY}
//                ,new Pizza[]{new Pizza("Meat Lover",1400), new Pizza("Vegan Delight", 1100)});
//
//        restaurants[2] = new Restaurant("Domino's Pizza - Edinburgh - Southside"
//                ,new LngLat(-3.1838572025299072,55.94449876875712)
//                ,new DayOfWeek[]{DayOfWeek.SATURDAY, DayOfWeek.SUNDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY}
//                ,new Pizza[]{new Pizza("Super Cheese",1400), new Pizza("All Shrooms", 900)});
//
//        restaurants[3] = new Restaurant("Sodeberg Pavillion"
//                ,new LngLat(-3.1940174102783203,55.94390696616939)
//                ,new DayOfWeek[]{DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY}
//                ,new Pizza[]{new Pizza("Proper Pizza",1400), new Pizza("Pineapple & Ham & Cheese", 900)});
//
//
//        Order sampleOrder = new Order();
//        sampleOrder.setOrderStatus(OrderStatus.UNDEFINED);
//        sampleOrder.setOrderValidationCode(OrderValidationCode.UNDEFINED);
//        sampleOrder.setOrderNo("19514FE0");
//        sampleOrder.setPriceTotalInPence(2400);
//        sampleOrder.setCreditCardInformation(creditCardSample);
//        sampleOrder.setOrderDate(date);
//        sampleOrder.setPizzasInOrder(pizzas);
//
//        OrderValidator validator = new OrderValidator();
//
//        validator.validateOrder(sampleOrder,restaurants);
//
//        System.out.println(sampleOrder.getOrderStatus());
//        System.out.println(sampleOrder.getOrderValidationCode());
//    }
//
////    @Test
////    public void testOrders(){
////        GetDataFromRest.setBaseUrl("https://ilp-rest.azurewebsites.net");
////
////        //If you want to test for a specific date, set startDate to the date you want to test and endDate to startDate+ 1 day
////        //Pick random dates in range of startDate and endDate
////        LocalDate startDate = LocalDate.of(2023, 9, 1);
////        LocalDate endDate = LocalDate.of(2024, 1, 28);
////        Set<LocalDate> randomDates = new HashSet<>();
////
////        //Adjust the number of dates you want to test
////        while (randomDates.size() < 1) {
////            LocalDate date = generateRandomDate(startDate, endDate);
////            randomDates.add(date);
////        }
////
////        for(LocalDate date : randomDates){
////            Restaurant[] restaurants = GetDataFromRest.getRestaurantsData();
////            Order[] ordersOnDay = GetDataFromRest.getOrdersOnDay(date);
////
////            for(Order order: ordersOnDay){
////                new OrderValidator().validateOrder(order,restaurants);
////
////                assertNotNull("Order status should not be null", order.getOrderStatus());
////                assertNotEquals("Order status should not be undefined", OrderStatus.UNDEFINED, order.getOrderStatus());
////
////                assertNotNull("Order validation code should not be null", order.getOrderValidationCode());
////                assertNotEquals("Order validation code should not be undefined", OrderValidationCode.UNDEFINED, order.getOrderValidationCode());
////
////            }
////            testAllValidationCodesPresent(ordersOnDay, date);
////        }
////    }
//
//    private void testAllValidationCodesPresent(Order[] ordersOnDay, LocalDate date) {
//        boolean isNoError = false;
//        boolean isCardNumberInvalid = false;
//        boolean isExpiryDateInvalid = false;
//        boolean isCvvInvalid = false;
//        boolean isTotalIncorrect = false;
//        boolean isPizzaNotDefined = false;
//        boolean isMaxPizzaCountExceeded = false;
//        boolean isPizzaFromMultipleRestaurants = false;
//        boolean isRestaurantClosed = false;
//
//        for (Order order : ordersOnDay) {
//            switch (order.getOrderValidationCode()) {
//                case NO_ERROR:
//                    isNoError = true;
//                    break;
//                case CARD_NUMBER_INVALID:
//                    isCardNumberInvalid = true;
//                    break;
//                case EXPIRY_DATE_INVALID:
//                    isExpiryDateInvalid = true;
//                    break;
//                case CVV_INVALID:
//                    isCvvInvalid = true;
//                    break;
//                case TOTAL_INCORRECT:
//                    isTotalIncorrect = true;
//                    break;
//                case PIZZA_NOT_DEFINED:
//                    isPizzaNotDefined = true;
//                    break;
//                case MAX_PIZZA_COUNT_EXCEEDED:
//                    isMaxPizzaCountExceeded = true;
//                    break;
//                case PIZZA_FROM_MULTIPLE_RESTAURANTS:
//                    isPizzaFromMultipleRestaurants = true;
//                    break;
//                case RESTAURANT_CLOSED:
//                    isRestaurantClosed = true;
//                    break;
//            }
//        }
//
//        assertTrue("OrderValidationCode NO_ERROR should be present for date " + date, isNoError);
//        assertTrue("OrderValidationCode CARD_NUMBER_INVALID should be present for date " + date, isCardNumberInvalid);
//        assertTrue("OrderValidationCode EXPIRY_DATE_INVALID should be present for date " + date, isExpiryDateInvalid);
//        assertTrue("OrderValidationCode CVV_INVALID should be present for date " + date, isCvvInvalid);
//        assertTrue("OrderValidationCode TOTAL_INCORRECT should be present for date " + date, isTotalIncorrect);
//        assertTrue("OrderValidationCode PIZZA_NOT_DEFINED should be present for date " + date, isPizzaNotDefined);
//        assertTrue("OrderValidationCode MAX_PIZZA_COUNT_EXCEEDED should be present for date " + date, isMaxPizzaCountExceeded);
//        assertTrue("OrderValidationCode PIZZA_FROM_MULTIPLE_RESTAURANTS should be present for date " + date, isPizzaFromMultipleRestaurants);
//        assertTrue("OrderValidationCode RESTAURANT_CLOSED should be present for date " + date, isRestaurantClosed);
//    }
//
//    private LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
//        Random random = new Random();
//        long startEpochDay = startDate.toEpochDay();
//        long endEpochDay = endDate.toEpochDay();
//        long randomEpochDay = startEpochDay + random.nextInt((int) (endEpochDay - startEpochDay));
//        return LocalDate.ofEpochDay(randomEpochDay);
//    }
//}
//

//}



//package uk.ac.ed.inf;
//
//import uk.ac.ed.inf.ilp.data.*;
//
//import java.time.DayOfWeek;
//import java.time.LocalDate;
//
//public class RestServiceReaderTest {
//
//    public static void main( String[] args ) {
//
//        //Get Restaurants from Rest
//        Restaurant[] rest = RestServiceReader.getRestaurant("https://ilp-rest.azurewebsites.net/");
//        for (Restaurant restaurant : rest) {
//            System.out.println(restaurant.name());
//            System.out.println(restaurant.location());
//            System.out.print("[");
//            for (DayOfWeek day : restaurant.openingDays()) {
//                System.out.print(day.toString() + ", ");
//            }
//            System.out.println("]");
//            for (Pizza pizza : restaurant.menu()) {
//                System.out.print(pizza.name() + ": ");
//                System.out.println(pizza.priceInPence());
//            }
//            System.out.println();
//        }
//
//
//        //Get Orders from Rest
//        Order[] ord = RestServiceReader.getOrder("https://ilp-rest.azurewebsites.net", LocalDate.ofEpochDay(2023-11-15));
//        for (Order order : ord) {
//            System.out.println(order.getOrderNo());
//            System.out.println(order.getOrderDate());
//            System.out.println(order.getOrderStatus());
//            System.out.println(order.getOrderValidationCode());
//            System.out.println(order.getPriceTotalInPence());
//            for (Pizza pizza : order.getPizzasInOrder()) {
//                System.out.print(pizza.name() + ": ");
//                System.out.println(pizza.priceInPence());
//            }
//            System.out.println();
//        }
//
//
//        //Get Central Area from Rest
//        NamedRegion centralArea = RestServiceReader.getCentralArea("https://ilp-rest.azurewebsites.net");
//        System.out.println(centralArea.name());
//        for (LngLat point : centralArea.vertices()) {
//            System.out.print("[" + point.lat() + ", ");
//            System.out.println(point.lng() + "]");
//        }
//        System.out.println();
//
//
//        //Get No-Fly Zones from Rest
//        NamedRegion[] noFlyZones = RestServiceReader.getNoFlyZones("https://ilp-rest.azurewebsites.net");
//        for (NamedRegion region : noFlyZones) {
//            System.out.println(region.name());
//            for (LngLat point : region.vertices()) {
//                System.out.print("[" + point.lat() + ", ");
//                System.out.println(point.lng() + "]");
//            }
//            System.out.println();
//        }
//    }
//}
