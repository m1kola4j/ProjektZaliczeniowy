package org.example.gym_app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Gym App API",
                version = "1.0.0",
                description = "REST API do zarządzania zajęciami i rezerwacjami w klubie fitness.",
                contact = @Contact(
                        name = "Zespół Gym App",
                        email = "support@gym-app.local"
                ),
                license = @License(name = "MIT License")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Środowisko lokalne")
        }
)
public class OpenApiConfig {
    // Konfiguracja oparta na adnotacjach — brak dodatkowego kodu.
}

