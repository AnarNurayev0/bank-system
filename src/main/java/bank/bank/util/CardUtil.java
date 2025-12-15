package bank.bank.util;

import java.time.LocalDate;

public class CardUtil {

    public static String generateCardNumber() {
        return "5274" + (long) (Math.random() * 1_0000_0000_0000L);
    }

    public static String generateCVV() {
        int cvv = (int) (Math.random() * 900) + 100;
        return String.valueOf(cvv);
    }

    public static LocalDate generateExpirationDate() {
        return LocalDate.now().plusYears(4);
    }

}
