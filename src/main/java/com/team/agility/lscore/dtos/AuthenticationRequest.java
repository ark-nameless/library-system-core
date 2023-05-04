package com.team.agility.lscore.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    @NotEmpty( message = "Email cannot be empty")
    @Size(min = 3, max = 255)
    @Email
    @Schema(description = "User's username", example = "user") 
    private String username;

    @NotEmpty(message = "Password cannot be empty")
    @Schema(description = "User's password", example = "s3cr3t")
    private String password;
}
