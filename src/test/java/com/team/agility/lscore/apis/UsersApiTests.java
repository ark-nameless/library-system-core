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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.event.annotation.BeforeTestExecution;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException.Conflict;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.AddPermissionToRoleDTO;
import com.team.agility.lscore.dtos.AuthenticationRequest;
import com.team.agility.lscore.dtos.ConflictErrorResponse;
import com.team.agility.lscore.dtos.CreateNewRoleDTO;
import com.team.agility.lscore.dtos.TokenResponse;
import com.team.agility.lscore.entities.Role;
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
public class UsersApiTests extends BaseApiIntegTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TokenResponse token;

    final String base_url = Endpoints.USERS_V1;
    final String permission_url = Endpoints.PERMISSIONS_V1;


    @BeforeEach
    void beforeAll() throws Exception {
        TokenResponse result = getTokenResponse();
        token = result;
    }

    @Test
    void testCheckIfUserEmailAlreadyExists_thenCreateNewUserWithSameEmail_expectToThrowAnException()
        throws Exception {
        
        MvcResult result = mockMvc.perform(get(base_url + "/" + DEFAULT_USER.getUsername())
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, composeAuthorization(token.getAccessToken())))
            .andReturn();
        
        try {
            parseResponseToObject(User.class, result);
        } catch(Exception e) {
            ConflictErrorResponse response = parseResponseToObject(ConflictErrorResponse.class, result);
            if (response.getStatus() == 409) {
                mockMvc.perform(post(Endpoints.AUTH_V1 + "/register")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(DEFAULT_USER)))
                    .andExpect(status().isCreated());
            }
        }
    }

    @Test
    void testCreateNewRoleAndAddPermissionAndAddRoleToUser_shouldUpdateUserPermissions() throws Exception {
        CreateNewRoleDTO newRole = new CreateNewRoleDTO("STUDENT");
        
        MvcResult newRoleResult = mockMvc.perform(
                post(permission_url + "/roles")
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(newRole)))
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        Role result = parseResponseToObject(Role.class, newRoleResult);
        log.trace("Result: {}", result);
        
        assertNotNull(result);
        

        AddPermissionToRoleDTO newPermission = new AddPermissionToRoleDTO("PERMISSION_TO_HELLO");
        createSampleUser();
        mockMvc.perform(
                put(permission_url + "/roles/" + result.getName() + "/add")
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(newPermission)))
            .andExpect(status().is2xxSuccessful());

        mockMvc.perform(
                put(base_url + "/" + DEFAULT_USER.getUsername() + "/role/" + result.getName())
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                )
            .andExpect(status().is2xxSuccessful());

        MvcResult getUser = mockMvc.perform(
                get(base_url + "/" + DEFAULT_USER.getUsername())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
                )
            .andExpect(status().is2xxSuccessful())
            .andReturn();
            
        User user = parseResponseToObject(User.class, getUser);
        assertNotNull(user.getRoles());
        deleteSampleUser();
    }

    @Test
    void testCreateNewUser_thenDeleteTheCreatedUser_shouldNotSave() throws Exception {
        createSampleUser();
        MvcResult result = mockMvc.perform(
            get(base_url + "/" + DEFAULT_USER.getUsername())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
        .andExpect(status().is2xxSuccessful())
        .andReturn();
        
        try {
            User user = parseResponseToObject(User.class, result);

            mockMvc.perform(
                    delete(Endpoints.USERS_V1 + "/" + user.getUsername() + "/delete")
                        .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
                )
                .andExpect(status().is2xxSuccessful());

            mockMvc.perform(
                    get(base_url + "/" + DEFAULT_USER.getUsername())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
                    )
                .andExpect(status().is4xxClientError());
        } catch(Exception e) {
            mockMvc.perform(post(Endpoints.AUTH_V1 + "/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(DEFAULT_USER)))
                .andExpect(status().isCreated());
        }
        deleteSampleUser();
    }


    @Test
    void testDeleteUser() throws Exception {
        deleteSampleUser();
        createSampleUser();
        mockMvc.perform(
                delete(Endpoints.USERS_V1 + "/" + DEFAULT_USER.getUsername() + "/delete")
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
            .andExpect(status().is2xxSuccessful());

        mockMvc.perform(
                get(Endpoints.USERS_V1 + "/" + DEFAULT_USER.getUsername())
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
            .andExpect(status().is4xxClientError());
    }

    @Test
    void testRegisterUsingExcelFile() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
            "file",
            "MOCK_DATA.xlsx",
            "multipart/form-data",
            new ClassPathResource("data/users_data.xlsx").getInputStream());

        mockMvc.perform(
                MockMvcRequestBuilders.multipart(Endpoints.USERS_V1 + "/register-excel")
                .file(mockMultipartFile)
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
            .andExpect(status().is2xxSuccessful());


        MvcResult result = mockMvc.perform(
                get(Endpoints.USERS_V1)
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        List<User> users = parseResponseToListOfObject(result, User.class);
        assertNotNull(users);
        assertTrue(users.size() > 90);
    }


    @Test
    void testUserActionsLockandUnlockUser() throws Exception {
        createSampleUser();
        AuthenticationRequest loginInfo = new AuthenticationRequest(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword());

        // lock user
        mockMvc.perform(
            put(Endpoints.USERS_V1 + "/" + DEFAULT_USER.getUsername() + "/lock")
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
        )
        .andExpect(status().isOk());
        
        // try logging in
        mockMvc.perform(
            post(Endpoints.AUTH_V1 + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginInfo))
        )
        .andExpect(status().is4xxClientError());

        // Unlock account
        mockMvc.perform(
            put(Endpoints.USERS_V1 + "/" + DEFAULT_USER.getUsername() + "/unlock")
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
        )
        .andExpect(status().isOk());

        // try logging in
        mockMvc.perform(
            post(Endpoints.AUTH_V1 + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginInfo))
        )
        .andExpect(status().isOk());

        deleteSampleUser();
    }

    @Test
    void testUserActionsDeactivateAndActivateAccount() throws Exception {
        createSampleUser();

        AuthenticationRequest loginInfo = new AuthenticationRequest(DEFAULT_USER.getUsername(), DEFAULT_USER.getPassword());

        // disable user
        mockMvc.perform(
            put(Endpoints.USERS_V1 + "/" + DEFAULT_USER.getUsername() + "/disable")
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
        )
        .andExpect(status().isOk());
        
        // try logging in
        mockMvc.perform(
            post(Endpoints.AUTH_V1 + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginInfo))
        )
        .andExpect(status().is4xxClientError());

        // enable account
        mockMvc.perform(
            put(Endpoints.USERS_V1 + "/" + DEFAULT_USER.getUsername() + "/enable")
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
        )
        .andExpect(status().isOk());

        // try logging in
        mockMvc.perform(
            post(Endpoints.AUTH_V1 + "/authenticate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginInfo))
        )
        .andExpect(status().isOk());

        deleteSampleUser();
    }

    void createSampleUser() throws Exception {
        mockMvc.perform(post(Endpoints.AUTH_V1 + "/register")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(DEFAULT_USER)))
            .andExpect(status().isCreated());
    }

    void deleteSampleUser() throws Exception {
        mockMvc.perform(
                delete(Endpoints.USERS_V1 + "/" + DEFAULT_USER.getUsername() + "/delete")
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            );
    }

}

