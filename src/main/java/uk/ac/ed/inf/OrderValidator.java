package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.OrderStatus;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;
import java.util.regex.*;

public class OrderValidator implements OrderValidation{
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {

        //CREDIT CARD
        if (!checkCredit(orderToValidate.getCreditCardInformation().getCreditCardNumber()) || orderToValidate.getCreditCardInformation().getCreditCardNumber().length() != 16 && orderToValidate.getCreditCardInformation().getCreditCardNumber().length() != 15 && orderToValidate.getCreditCardInformation().getCreditCardNumber().length() != 19)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        //EXPIRY DATE
        if (isExpired(orderToValidate.getCreditCardInformation().getCreditCardExpiry()) || orderToValidate.getCreditCardInformation().getCreditCardExpiry() == null)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }

        //CVV
        if (!checkCVV(orderToValidate.getCreditCardInformation().getCvv()))
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        //PIZZA NOT DEFINED
        if (!checkDefined(orderToValidate, definedRestaurants))
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            return orderToValidate;
        }

        //MAX PIZZA COUNT EXCEEDED
        if (orderToValidate.getPizzasInOrder().length < 1 || orderToValidate.getPizzasInOrder().length > 5 )
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            return orderToValidate;
        }

        //PIZZA FROM MULTIPLE RESTAURANTS
        if (checkMultiRest(orderToValidate, definedRestaurants))
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            return orderToValidate;
        }

        //RESTAURANT CLOSED
        if (checkClosed(orderToValidate, definedRestaurants))
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            return orderToValidate;
        }

        //TOTAL INCORRECT
        if (getPrice(orderToValidate, definedRestaurants) != orderToValidate.getPriceTotalInPence() - 100)
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            return orderToValidate;
        }

        //NO ERROR
        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        return orderToValidate;
    }

    private static boolean checkCredit(String cardNo)
    {
        int Digits = cardNo.length();

        int Sum = 0;
        boolean isSecond = false;
        for (int i = Digits - 1; i >= 0; i--)
        {

            int d = cardNo.charAt(i) - '0';

            if (isSecond)
                d = d * 2;

            Sum += d / 10;
            Sum += d % 10;

            isSecond = !isSecond;
        }
        return (Sum % 10 == 0);
    }

    private static boolean isExpired (String expDate) {

        SimpleDateFormat SDF = new SimpleDateFormat("MM/yy");
        SDF.setLenient(false);

        Date expiry;

        try {
            expiry = SDF.parse(expDate);
        } catch (ParseException e) {
            return true;
        }

        return expiry.before(new Date());
    }

    private static boolean checkCVV(String str)
    {
        String regex = "^[0-9]{3}$";

        Pattern p = Pattern.compile(regex);

        if (str == null)
        {
            return false;
        }

        Matcher m = p.matcher(str);

        return m.matches();
    }

    private static boolean checkDefined(Order orderToValidate, Restaurant[] definedRestaurants)
    {
        for (int i = 0; i < orderToValidate.getPizzasInOrder().length; i += 1)
        {
            boolean contains = false;
            for (Restaurant definedRestaurant : definedRestaurants)
            {
                for (int k = 0; k < definedRestaurant.menu().length; k += 1)
                {
                    if (Objects.equals(definedRestaurant.menu()[k].name(), (orderToValidate.getPizzasInOrder()[i]).name()))
                    {
                        contains = true;
                        break;
                    }
                }
            }

            if (!contains)
            {
                return contains;
            }
        }
        return true;
    }

    private static boolean checkMultiRest (Order orderToValidate, Restaurant[] definedRestaurants) {
        String restaurantName = null;
        boolean multipleRestaurant = false;
        for (int i = 0; i < orderToValidate.getPizzasInOrder().length; i += 1)
        {
            for (Restaurant definedRestaurant : definedRestaurants)
            {
                for (int k = 0; k < definedRestaurant.menu().length; k += 1)
                {
                    if (Objects.equals(definedRestaurant.menu()[k].name(), (orderToValidate.getPizzasInOrder()[i]).name())) {
                        if (restaurantName == null)
                        {
                            restaurantName = definedRestaurant.name();
                            break;
                        }
                        else if (!Objects.equals(definedRestaurant.name(), restaurantName))
                        {
                            multipleRestaurant = true;
                        }
                    }
                }
            }
        }
        return multipleRestaurant;
    }

    private static boolean checkClosed(Order orderToValidate, Restaurant[] definedRestaurants) {
        for (int i = 0; i < orderToValidate.getPizzasInOrder().length; i += 1)
        {
            boolean invalid = true;
            for (Restaurant definedRestaurant : definedRestaurants)
            {
                for (int k = 0; k < definedRestaurant.menu().length; k += 1)
                {
                    if (Objects.equals(definedRestaurant.menu()[k].name(), (orderToValidate.getPizzasInOrder()[i]).name())) {
                        for (int t = 0; t < definedRestaurant.openingDays().length; t += 1) {
                            if (definedRestaurant.openingDays()[t] == (orderToValidate.getOrderDate().getDayOfWeek())) {
                                invalid = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (invalid)
            {
                return invalid;
            }
        }
        return false;
    }

    private static int getPrice(Order orderToValidate, Restaurant[] definedRestaurants) {
        int price = 0;
        for (int i = 0; i < orderToValidate.getPizzasInOrder().length; i += 1)
        {
            for (Restaurant definedRestaurant : definedRestaurants)
            {
                for (int k = 0; k < definedRestaurant.menu().length; k += 1)
                {
                    if (Objects.equals(definedRestaurant.menu()[k].name(), orderToValidate.getPizzasInOrder()[i].name()))
                    {
                        price = price + definedRestaurant.menu()[k].priceInPence();
                    }
                }
            }
        }
        return price;
    }
}
