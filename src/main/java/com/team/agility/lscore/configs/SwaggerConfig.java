package com.team.agility.lscore.configs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {
    @Bean
	public OpenAPI myOpenAPI() {
		Contact contact = new Contact();
		contact.setEmail("ark.nameless.zero@gmail.com");
		contact.setName("Ark Nameless");

		Server localServer = new Server();
		localServer.setUrl("http://localhost:9999");
		localServer.setDescription("Server URL in Local environment");
			
		License mitLicense = new License()
				.name("MIT License")
				.url("https://choosealicense.com/licenses/mit/");

		Info info = new Info()
				.title("RESTful API for Library System")
				.contact(contact)
				.version("0.0.1")
				.description("This API contains the endpoints for a library system")
				// .termsOfService("https://my-awesome-api.com/terms")
				.license(mitLicense);

		return new OpenAPI()
				.info(info)
				.servers(List.of(localServer));
	}
	
}
