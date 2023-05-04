package com.team.agility.lscore.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewUserDTO {

    @Schema(description = "User first name",
        example = "Ark")
    private String firstname;

    @Schema(description = "User last name.",
        example = "Zero")
    private String lastname;

    @NotBlank
    @Size(min = 3, max = 255)
    @Schema(description = "User email that will serve as the email authentication. Must be unique",
        example = "user@example.com")
    private String email;

    @NotBlank
    @Size(min = 3, max = 255)
    @Schema(description = "User username that will serve as the username for authorization. Must be unique",
        example = "ark_zero")
    private String username;

    @NotBlank
    @Schema(description = "User's password", example = "s3cr3t")
    private String password;
}
