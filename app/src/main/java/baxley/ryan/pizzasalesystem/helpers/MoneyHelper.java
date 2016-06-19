package baxley.ryan.pizzasalesystem.helpers;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Contains static methods for money related assistance
 */
public class MoneyHelper {
    /**
     * Converts a BigDecimal to a dollar format
     */
    public static String currencyFormat(BigDecimal n) {
        return NumberFormat.getCurrencyInstance().format(n);
    }
}
