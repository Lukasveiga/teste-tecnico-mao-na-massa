package com.attus.testetecnico.system;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(

        info = @Info(
                contact = @Contact(
                        name = "Lukas Veiga",
                        email = "lukas.veiga10@gmail.com",
                        url = "https://github.com/Lukasveiga"
                ),
                description = "OpenApi Documentation: Attus Teste Técnico - API de Gestão de Pessoas",
                title = "Person Management RestApi Documentation",
                version = "0.0.1"
        ),
        servers = {
                @Server(
                        description = "Dev env",
                        url = "http://localhost:8080/api/v1"
                ),
                @Server(
                        description = "Prod env",
                        url = "http://localhost:8080/api/v1"
                )

        }
)
public class OpenApiConfig {}
