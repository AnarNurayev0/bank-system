package bank.bank.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class EmailTemplateUtil {

    private static String template;

    static {
        try {
            template = new String(Files.readAllBytes(Paths.get("src/main/resources/templates/email-template.html")));
        } catch (IOException e) {
            throw new RuntimeException("E-poçt şablonu oxunarkən xəta baş verdi", e);
        }
    }

    public static String getFormattedEmail(String content) {
        return template.replace("{content}", content);
    }
}
