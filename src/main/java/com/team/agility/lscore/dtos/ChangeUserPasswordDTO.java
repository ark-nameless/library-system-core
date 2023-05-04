package com.team.agility.lscore.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeUserPasswordDTO {
    @NotBlank
    @Size(min = 3, max = 255)
    @Schema(description = "User's current password")
    private String currentPassword;

    @NotBlank
    @Size(min = 3, max = 255)
    @Schema(description = "User's new password")
    private String newPassword;
}

