package bank.bank.util;

public class MaskUtil {

    public static String maskName(String fullName) {
        if (fullName.length() < 2)
            return fullName + "***";

        return fullName.substring(0, 2) + "***";
    }
}
