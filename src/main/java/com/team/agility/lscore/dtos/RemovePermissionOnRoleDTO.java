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
public class RemovePermissionOnRoleDTO {
    @NotBlank
    @Size(min = 3, max = 255)
    @Schema(description = "The privilege name that will be removed in the selected role.",
        example = "MANAGE_ALL_ENDPOINTS")
    private String name;
    
}

