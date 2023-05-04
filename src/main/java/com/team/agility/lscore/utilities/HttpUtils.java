package com.team.agility.lscore.utilities;

import org.springframework.http.HttpMethod;

import lombok.experimental.UtilityClass;

@UtilityClass
public class HttpUtils {
    
    public HttpMethod toHttpMethod(String method) {
        HttpMethod converted = null;
        if (method.contains("GET")) converted = HttpMethod.GET;
        else if (method.contains("DELETE")) converted = HttpMethod.DELETE;
        else if (method.contains("GET")) converted = HttpMethod.GET;
        else if (method.contains("HEAD")) converted = HttpMethod.HEAD;
        else if (method.contains("OPTIONS")) converted = HttpMethod.OPTIONS;
        else if (method.contains("PATCH")) converted = HttpMethod.PATCH;
        else if (method.contains("POST")) converted = HttpMethod.POST;
        else if (method.contains("PUT")) converted = HttpMethod.PUT;
        else if (method.contains("TRACE")) converted = HttpMethod.TRACE;

        return converted;
    }
}
