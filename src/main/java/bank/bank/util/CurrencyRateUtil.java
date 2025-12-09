package bank.bank.util;

import java.math.BigDecimal;

public class CurrencyRateUtil {

    public static BigDecimal getRate(String from, String to) {

        if (from.equals(to))
            return BigDecimal.ONE;

        if (from.equals("USD") && to.equals("AZN"))
            return new BigDecimal("1.70");
        if (from.equals("AZN") && to.equals("USD"))
            return new BigDecimal("0.588");

        if (from.equals("EUR") && to.equals("AZN"))
            return new BigDecimal("1.80");
        if (from.equals("AZN") && to.equals("EUR"))
            return new BigDecimal("0.555");

        if (from.equals("USD") && to.equals("EUR"))
            return new BigDecimal("0.94");
        if (from.equals("EUR") && to.equals("USD"))
            return new BigDecimal("1.06");

        throw new RuntimeException("This currency pair is not supported");
    }

}
