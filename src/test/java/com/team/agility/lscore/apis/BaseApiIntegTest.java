package com.team.agility.lscore.apis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.AddPermissionToRoleDTO;
import com.team.agility.lscore.dtos.AuthenticationRequest;
import com.team.agility.lscore.dtos.CreateNewRoleDTO;
import com.team.agility.lscore.dtos.CreateNewUserDTO;
import com.team.agility.lscore.dtos.TokenResponse;

import lombok.extern.log4j.Log4j2;

@Log4j2
@TestMethodOrder(MethodOrderer.MethodName.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BaseApiIntegTest {
    
    protected final String SUPER_ADMIN_EMAIL = "admin@email.com";
    protected final String SUPER_ADMIN_USERNAME = "admin";
    protected final String SUPER_ADMIN_PASSWORD = "admin";
    protected final String DEFAULT_PASSWORD = "s3cr3t";
    protected final CreateNewUserDTO DEFAULT_USER = new CreateNewUserDTO
        ("ark", "nameless", "ark.nameless.zero@gmail.com", "ark_zero", DEFAULT_PASSWORD);

        
    protected static CreateNewRoleDTO SAMPLE_ROLE 
        = new CreateNewRoleDTO("STUDENT");
    protected static AddPermissionToRoleDTO SUPER_ADMIN_PERMISSION 
        = new AddPermissionToRoleDTO("PERMISSION_TO_ACCESS_ALL");


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    protected TokenResponse getTokenResponse() throws Exception {
        AuthenticationRequest form = new AuthenticationRequest();
        form.setUsername(SUPER_ADMIN_USERNAME);
        form.setPassword(SUPER_ADMIN_PASSWORD);

        log.trace("Get token reponse");
        MvcResult result = mockMvc.perform(post(Endpoints.AUTH_V1 + "/authenticate")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(form)))
            .andExpect(status().isOk())
            .andReturn();

        return parseResponseToObject(TokenResponse.class, result);
    }

    protected <T> T parseResponseToObject(Class<T> clazz, MvcResult mvcResult)
            throws UnsupportedEncodingException, JsonProcessingException {
        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
    }

    protected <T> List<T> parseResponseToListOfObject(MvcResult result, Class<T> clazz) throws Exception {
        String responseBody = result.getResponse().getContentAsString();
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return objectMapper.readValue(responseBody, type);
    }

    protected String composeAuthorization(String token) {
        return Constants.TOKEN_TYPE.concat(" ") + token;
    }
}
