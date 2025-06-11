package is.yarr.beerscanner.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

/**
 * Configuration for OpenAPI documentation.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure OpenAPI documentation.
     *
     * @return the OpenAPI configuration
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Beer Scanner API")
                        .description("API for the Beer Scanner application, which tracks beer availability at bars")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Beer Scanner Team")
                                .email("info@beerscanner.com")
                                .url("https://beerscanner.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server().url("/").description("Default Server URL")))
                .components(new Components()
                        .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}