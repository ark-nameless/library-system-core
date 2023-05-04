package com.team.agility.lscore.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTokenExpirationDTO {
    @PositiveOrZero
    @Schema(description = "Update token's expiration in days(0..n)",
        required = true
    )
    private int days;

    @PositiveOrZero
    @Schema(description = "Update token's expiration in hours(0..n)",
        required = true
    )
    private int hours;

    @PositiveOrZero
    @Schema(description = "Update token's expiration in minutes(0..n)",
        required = true
    )
    private int minutes;
}
