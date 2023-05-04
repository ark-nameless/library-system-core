package com.team.agility.lscore.dtos;

import com.team.agility.lscore.constants.HttpConsts;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadRequestErrorResponse {
    @Schema(description = "Http Method name", example = "GET", allowableValues = {"GET", "POST", "PUT", "DELETE"})
    private String method;

    @Schema(description = "Service URI", example = "/ls-core")
    private String path;

    @Schema(description = "Http Status code", example = HttpConsts.HTTP_BAD_REQUEST_CODE)
    private int status;

    @Schema(description = "Http Status description", example = HttpConsts.HTTP_BAD_REQUEST_DESC)
    private String error;
}