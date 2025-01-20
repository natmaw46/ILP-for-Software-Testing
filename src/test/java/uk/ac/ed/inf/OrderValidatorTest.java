package uk.ac.ed.inf;

import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OrderValidatorTest extends TestCase {
    final private OrderValidator validator = new OrderValidator();


    // ------------------------ [builders] ------------------------

    private Order buildStage1Order() {
        final Order order = new Order();
        order.setOrderNo("foobar");
        order.setOrderDate(LocalDate.parse("2023-12-01"));
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);
        return order;
    }

    private Order buildStage2Order() {
        final Order order = buildStage1Order();

        final CreditCardInformation cardInformation = new CreditCardInformation();
        cardInformation.setCvv("952");
        cardInformation.setCreditCardNumber("5555555555554444");
        cardInformation.setCreditCardExpiry("06/38");

        order.setCreditCardInformation(cardInformation);
        return order;
    }

    private Restaurant buildRestaurant(Pizza[] items) {
        return new Restaurant(
                "Restaurant",
                null,
                new DayOfWeek[]{DayOfWeek.FRIDAY},
                items
        );
    }

    // ------------------------ [context tests] ------------------------

    public void testContext_OrderNumber() {
        final Order order = new Order();
        validator.validateOrder(order, new Restaurant[]{null});

        assertEquals(OrderValidationCode.UNDEFINED, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    public void testContext_OrderStatus() {
        final Order order = new Order();
        order.setOrderNo("foobar");
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);

        validator.validateOrder(order, new Restaurant[]{null});

        assertEquals(OrderValidationCode.UNDEFINED, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    public void testContext_OrderValidation() {
        final Order order = new Order();
        order.setOrderNo("foobar");
        order.setOrderDate(LocalDate.now());
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.NO_ERROR);

        validator.validateOrder(order, new Restaurant[]{null});

        assertEquals(OrderValidationCode.UNDEFINED, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    // ------------------------ [credit card validation tests] ------------------------

    public void testCard_CVV() {
        final String[] cases = {"12", "1234", "a12", "a123", "@12"};
        for (String cvv : cases) {
            final CreditCardInformation cardInformation = new CreditCardInformation();
            cardInformation.setCvv(cvv);

            final Order order = buildStage1Order();
            order.setCreditCardInformation(cardInformation);

            validator.validateOrder(order, new Restaurant[]{null});

            assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        }
    }

    public void testCard_Number() {
        final CreditCardInformation cardInformation = new CreditCardInformation();
        cardInformation.setCvv("123");

        final String[] cases = {
                "123456789012345", "a1234567890123456", "@1234567890123456", "123456789012345678", "abcde"
        };
        for (String card : cases) {
            cardInformation.setCreditCardNumber(card);

            final Order order = buildStage1Order();
            order.setCreditCardInformation(cardInformation);

            validator.validateOrder(order, new Restaurant[]{null});

            assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        }
    }

    public void testCard_ExpiryDate() {
        final CreditCardInformation cardInformation = new CreditCardInformation();
        cardInformation.setCvv("123");
        cardInformation.setCreditCardNumber("1234567890123456");

        final String[] cases = {"12/22", "13/23", "a1/23", "-1/23", "1/23", "01/a", "0a/0b"};
        for (String date : cases) {
            cardInformation.setCreditCardExpiry(date);

            final Order order = buildStage1Order();
            order.setCreditCardInformation(cardInformation);

            validator.validateOrder(order, new Restaurant[]{null});

            assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        }
    }

    // ------------------------ [restaurant validation tests] ------------------------

    public void testRestaurant_PizzaCount() {
        final Map<OrderValidationCode, Pizza[]> cases = new HashMap<>();
        cases.put(OrderValidationCode.PIZZA_NOT_DEFINED, new Pizza[]{});
        cases.put(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, new Pizza[]{
                new Pizza("", 1), new Pizza("", 1), new Pizza("", 1), new Pizza("", 1), new Pizza("", 1),
        });

        for (OrderValidationCode code : cases.keySet()) {
            final Order order = buildStage2Order();
            order.setPizzasInOrder(cases.get(code));

            validator.validateOrder(order, new Restaurant[]{null});

            assertEquals(code, order.getOrderValidationCode());
            assertEquals(OrderStatus.INVALID, order.getOrderStatus());
        }
    }

    public void testRestaurant_Menu() {
        final Order order = buildStage2Order();
        order.setPizzasInOrder(new Pizza[]{new Pizza("foobar", 1)});

        final Restaurant restaurant = new Restaurant(
                "Restaurant", null, null, new Pizza[]{new Pizza("barfoo", 1)}
        );

        validator.validateOrder(order, new Restaurant[]{restaurant});

        assertEquals(OrderValidationCode.PIZZA_NOT_DEFINED, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    public void testRestaurant_OpenDays() {
        final Pizza pizza = new Pizza("foobar", 1);
        final Order order = buildStage2Order();
        order.setPizzasInOrder(new Pizza[]{pizza});

        final Restaurant restaurant = new Restaurant(
                "Restaurant", null, new DayOfWeek[]{}, new Pizza[]{pizza}
        );

        validator.validateOrder(order, new Restaurant[]{restaurant});

        assertEquals(OrderValidationCode.RESTAURANT_CLOSED, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    public void testRestaurant_SingleRestaurant() {
        Pizza pizza = new Pizza("foobar", 1);
        final Restaurant restaurant1 = buildRestaurant(new Pizza[]{pizza});

        pizza = new Pizza("", 1);
        final Restaurant restaurant2 = buildRestaurant(new Pizza[]{pizza});

        final Order order = buildStage2Order();
        order.setPizzasInOrder(new Pizza[]{
                pizza, new Pizza("foobar", 1), new Pizza("", 1),
        });

        validator.validateOrder(order, new Restaurant[]{restaurant1, restaurant2});

        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    // ------------------------ [order total validation tests] ------------------------

    public void testTotal() {
        final Pizza pizza1 = new Pizza("foobar", 1);
        final Pizza pizza2 = new Pizza("barfoo", 1);
        final Restaurant restaurant = buildRestaurant(new Pizza[]{pizza1, pizza2});

        final Order order = buildStage2Order();
        order.setPizzasInOrder(new Pizza[]{pizza1, pizza2});
        order.setPriceTotalInPence(SystemConstants.ORDER_CHARGE_IN_PENCE);

        validator.validateOrder(order, new Restaurant[]{restaurant});

        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    public void testTotal_IncludeDeliveryCharge() {
        final Pizza pizza1 = new Pizza("foobar", 1);
        final Pizza pizza2 = new Pizza("barfoo", 1);
        final Restaurant restaurant = buildRestaurant(new Pizza[]{pizza1, pizza2});

        final Order order = buildStage2Order();
        order.setPizzasInOrder(new Pizza[]{pizza1, pizza2});
        order.setPriceTotalInPence(
                Arrays.stream(order.getPizzasInOrder()).map(Pizza::priceInPence).reduce(0, Integer::sum)
        );

        validator.validateOrder(order, new Restaurant[]{restaurant});

        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());
        assertEquals(OrderStatus.INVALID, order.getOrderStatus());
    }

    public void testDeliverableOrder() {
        final Pizza pizza1 = new Pizza("foobar", 250);
        final Pizza pizza2 = new Pizza("barfoo", 500);
        final Restaurant restaurant = buildRestaurant(new Pizza[]{pizza1, pizza2});

        final Order order = buildStage2Order();
        order.setPizzasInOrder(new Pizza[]{pizza1, pizza2});
        order.setPriceTotalInPence(
                Arrays.stream(order.getPizzasInOrder())
                        .map(Pizza::priceInPence)
                        .reduce(SystemConstants.ORDER_CHARGE_IN_PENCE, Integer::sum)
        );

        validator.validateOrder(order, new Restaurant[]{restaurant});

        assertEquals(OrderValidationCode.NO_ERROR, order.getOrderValidationCode());
        assertEquals(OrderStatus.VALID_BUT_NOT_DELIVERED, order.getOrderStatus());
    }

    public void testIllegalArgumentException_Order() {
        try {
            validator.validateOrder(null, null);
            fail("expected 'IllegalArgumentException' to be thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("order"));
        }
    }

    public void testIllegalArgumentException_Restaurants() {
        final Restaurant[][] cases = {null, new Restaurant[]{}};
        for (Restaurant[] restaurants : cases) {
            try {
                validator.validateOrder(buildStage2Order(), restaurants);
                fail("expected 'IllegalArgumentException' to be thrown");
            } catch (IllegalArgumentException e) {
                assertTrue(e.getMessage().contains("restaurants"));
            }
        }
    }
}

