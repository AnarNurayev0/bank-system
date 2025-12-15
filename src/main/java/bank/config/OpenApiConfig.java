package bank.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankSystemOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank System API")
                        .description("API documentation for the Bank System application")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Bank System Team")
                                .email("nbanknotifications@gmail.com")));
    }
}
