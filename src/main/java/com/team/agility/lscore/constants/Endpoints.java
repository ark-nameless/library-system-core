package com.team.agility.lscore.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Endpoints {
    private final String COMMON_ENDPOINT_V1 = "/api/v1";

    public final String USERS_V1 = COMMON_ENDPOINT_V1 + "/users";
    public final String ROLES_V1 = COMMON_ENDPOINT_V1 + "/roles";
    public final String PRIVILEGES_V1 = COMMON_ENDPOINT_V1 + "/privileges";
    public final String ENDPOINTS_V1 = COMMON_ENDPOINT_V1 + "/endpoints";
    public final String TOKEN_EXPIRATION_V1 = COMMON_ENDPOINT_V1 + "/token-expiration";
    public final String PERMISSIONS_V1 = COMMON_ENDPOINT_V1 + "/permissions";
    public final String AUTH_V1 = COMMON_ENDPOINT_V1 + "/auth";
    public final String BOOKS_V1 = COMMON_ENDPOINT_V1 + "/books";
    public final String LOGS_V1 = COMMON_ENDPOINT_V1 + "/logs";
    public final String BORROW_BOOKS_V1 = COMMON_ENDPOINT_V1 + "/borrow";

    public final String HEALTH_V1 = COMMON_ENDPOINT_V1 + "/health";
    public final String[] SWAGGER_V3 = {
        "/v3/api-docs/**",
        "/swagger-ui/**"
    };
}