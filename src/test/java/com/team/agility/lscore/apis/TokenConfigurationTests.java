package com.team.agility.lscore.apis;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException.Conflict;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.AddPermissionToRoleDTO;
import com.team.agility.lscore.dtos.ConflictErrorResponse;
import com.team.agility.lscore.dtos.CreateNewRoleDTO;
import com.team.agility.lscore.dtos.TokenResponse;
import com.team.agility.lscore.dtos.UpdateTokenExpirationDTO;
import com.team.agility.lscore.entities.Role;
import com.team.agility.lscore.entities.TokenExpiration;
import com.team.agility.lscore.entities.User;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TokenConfigurationTests extends BaseApiIntegTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TokenResponse token;

    @BeforeEach
    void beforeAll() throws Exception {
        TokenResponse result = getTokenResponse();
        token = result;
    }

    @Test
    void testGetAllTokenConfiguration() throws Exception {
        
        MvcResult result = mockMvc.perform(
                get(Endpoints.TOKEN_EXPIRATION_V1)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        @SuppressWarnings("unchecked")
        List<TokenExpiration> response = (List<TokenExpiration>) parseResponseToObject(List.class, result);

        assertTrue(response.size() > 0);
    }

    @Test
    void testTokenExpirationUpdate() throws Exception {
        MvcResult result = mockMvc.perform(
                get(Endpoints.TOKEN_EXPIRATION_V1)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        
        List<TokenExpiration> response = parseResponseToListOfObject(result, TokenExpiration.class);
        log.trace("Token Expirations: {}", response);
        var tokenToUpdate = response.get(0);
        UpdateTokenExpirationDTO updateTokenExpiration =  
            UpdateTokenExpirationDTO.builder()
                .days(tokenToUpdate.getDays() + 1)
                .hours(tokenToUpdate.getHours() + 1)
                .minutes(tokenToUpdate.getMinutes() + 10)
                .build();

        MvcResult updateResult = mockMvc.perform(
                put(Endpoints.TOKEN_EXPIRATION_V1 + "/" + tokenToUpdate.getName())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(updateTokenExpiration))
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        TokenExpiration updateResponse = parseResponseToObject(TokenExpiration.class, updateResult);

        log.trace("{} = {} = {}", tokenToUpdate, updateResponse, updateTokenExpiration);
        assertNotEquals(tokenToUpdate.getDays(), updateResponse.getDays());
        assertNotEquals(tokenToUpdate.getHours(), updateResponse.getHours());
        assertNotEquals(tokenToUpdate.getMinutes(), updateResponse.getMinutes());
        
    }
}

