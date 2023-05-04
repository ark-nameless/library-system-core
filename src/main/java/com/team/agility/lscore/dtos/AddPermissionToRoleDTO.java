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
public class AddPermissionToRoleDTO {
    @NotBlank
    @Size(min = 3, max = 255)
    @Schema(description = "The new privilege that will be added to the selected role. Must be unique",
        example = "PERMISSION_TO_MANAGE_ALL_ENDPOINTS")
    private String name;
}