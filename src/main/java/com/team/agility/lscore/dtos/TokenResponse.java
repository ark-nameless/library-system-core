package com.team.agility.lscore.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    @Schema(description = "User's access token", example = "05976e7d6c76d6391f2710d30b8dc0a03e5ec59b2ff2e76072b13664b848c28c") 
    private String accessToken;

    @Schema(description = "User's refresh token", example = "eddec08937a214e58b718248fc59d60dc56d443c6798552523ca5f7936ae2120") 
    private String refreshToken;
}
