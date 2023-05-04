package com.team.agility.lscore.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public final String REQUEST_URI = "user_template";
    public final String SECURITY_REQUIREMENT = "Authorization";
    public final String JWT_CLAIM_PERMISSIONS = "permissions";
    public final String JWT_CLAIM_ROLES = "roles";
    public final String TOKEN_TYPE = "Bearer";
    public final String ACCESS_TOKEN = "access_token";
    public final String REFRESH_TOKEN = "refresh_token";
    public final Integer MAX_INVALID_ATTEMPT = 5;
    public final String DEFAULT_PASSWORD = "opoOBT2yssGyLdCm";
}
