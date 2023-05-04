package com.team.agility.lscore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.team.agility.lscore.constants.Constants;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;


@SecurityScheme(name = Constants.SECURITY_REQUIREMENT,
	description = "JWT Authorization header using the Bearer scheme",
	scheme = "Bearer",
	bearerFormat = "JWT",
	type = SecuritySchemeType.HTTP,
	in = SecuritySchemeIn.HEADER)
@SpringBootApplication
public class LsCoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(LsCoreApplication.class, args);
	}

}
