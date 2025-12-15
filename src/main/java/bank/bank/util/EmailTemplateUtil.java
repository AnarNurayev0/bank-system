package bank.bank.util;

import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class EmailTemplateUtil {

    private static String template;

    static {
        try {
            ClassPathResource resource = new ClassPathResource("templates/email-template.html");
            try (InputStream inputStream = resource.getInputStream()) {
                template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while reading the email template", e);
        }
    }

    public static String getFormattedEmail(String content) {
        return template.replace("{content}", content);
    }
}
