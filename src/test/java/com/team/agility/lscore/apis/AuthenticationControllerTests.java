package com.team.agility.lscore.apis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.AuthenticationRequest;
import com.team.agility.lscore.dtos.CreateNewUserDTO;
import com.team.agility.lscore.dtos.TokenResponse;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.services.UserService;

import lombok.extern.log4j.Log4j2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTests extends BaseApiIntegTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    private TokenResponse token;

    @BeforeEach
    void beforeAll() throws Exception {
        TokenResponse result = getTokenResponse();
        token = result;
    }


    @Test
    void testUserRegistrationIsSuccessful() throws Exception {
        CreateNewUserDTO userRegister = new CreateNewUserDTO("Ark", "Zero", "ark@gmail.com", "ark_zero", "ark999");

        log.trace("Testing public registration of user");
        mockMvc.perform(post(Endpoints.AUTH_V1 + "/register")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(userRegister)))
            .andExpect(status().isCreated());

        
        User isUserRegistered = userService.findByEmail("ark@gmail.com");
        assertNotEquals(isUserRegistered, null);
    }

    @Test
    void testUserLoginWillReturnAccessTokenAndRefreshTokenAndPerformBasicAuthorization() throws Exception {
        assertNotEquals(token.getAccessToken(), null);
        assertNotEquals(token.getRefreshToken(), null);

        mockMvc.perform(get("/api/v1/logs")
            .header(AUTHORIZATION, composeAuthorization(token.getAccessToken()))
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk());
    }

}
