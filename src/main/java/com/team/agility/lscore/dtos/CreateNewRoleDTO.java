package com.team.agility.lscore.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewRoleDTO {
    @NotEmpty
    @Schema(description = "Role name", example = "User") 
    private String name;
}