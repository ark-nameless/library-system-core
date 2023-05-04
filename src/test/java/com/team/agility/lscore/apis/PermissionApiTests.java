package com.team.agility.lscore.apis;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.AddPermissionToRoleDTO;
import com.team.agility.lscore.dtos.ConflictErrorResponse;
import com.team.agility.lscore.dtos.CreateNewRoleDTO;
import com.team.agility.lscore.dtos.RemovePermissionOnRoleDTO;
import com.team.agility.lscore.dtos.TokenResponse;
import com.team.agility.lscore.entities.Role;
import com.team.agility.lscore.entities.User;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PermissionApiTests extends BaseApiIntegTest {
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
    void testGetAllRole_shouldGetWhenAuthenticatedAndNot() throws Exception {
        mockMvc.perform(
            get(Endpoints.PERMISSIONS_V1 + "/roles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().is4xxClientError());
        
        mockMvc.perform(
            get(Endpoints.PERMISSIONS_V1 + "/roles")
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().is2xxSuccessful());
    }
    
    @Test
    void testGettingPrivileges() throws Exception {
        mockMvc.perform(
                get(Endpoints.PERMISSIONS_V1 + "/privileges")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().is4xxClientError());
        
        mockMvc.perform(
                get(Endpoints.PERMISSIONS_V1 + "/privileges")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGettingASinglePrivilegeUsingPrivilegeName() throws Exception {
        mockMvc.perform(
                get(Endpoints.PERMISSIONS_V1 + "/privileges")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().is4xxClientError());
        
        mockMvc.perform(
                get(Endpoints.PERMISSIONS_V1 + "/privileges")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testGetARoleUsingRoleName_andTestFailExceptions() throws Exception {
        createSampleRole();

        mockMvc.perform(
                get(Endpoints.PERMISSIONS_V1 + "/roles/" + SAMPLE_ROLE.getName())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            )
            .andExpect(status().is4xxClientError());
        
        mockMvc.perform(
                get(Endpoints.PERMISSIONS_V1 + "/roles/" + SAMPLE_ROLE.getName())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
            .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testRemovePermissionInRole_andFailIfNotExists() throws Exception {
        createSampleRole();
        addPermissionToSampleRole();

        RemovePermissionOnRoleDTO removeRole = new RemovePermissionOnRoleDTO(SUPER_ADMIN_PERMISSION.getName());

        MvcResult result = mockMvc.perform(
                put(Endpoints.PERMISSIONS_V1 + "/roles/" + SAMPLE_ROLE.getName() + "/remove")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(removeRole))
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))   
            )
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        Role role = parseResponseToObject(Role.class, result);
        assertTrue(role.getPermissions().size() <= 0, "Role expected to remove permission");
    }

    @Test
    void testAddPermissionToExistingRole_andFailIfNotExists() throws Exception {
        createSampleRole();

        MvcResult result = mockMvc.perform(
                get(Endpoints.PERMISSIONS_V1 + "/roles/" + SAMPLE_ROLE.getName())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        Role role = parseResponseToObject(Role.class, result);
        
        MvcResult updateResult = mockMvc.perform(
                put(Endpoints.PERMISSIONS_V1 + "/roles/" + SAMPLE_ROLE.getName() + "/add")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(SUPER_ADMIN_PERMISSION))
                    .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
            )
            .andExpect(status().is2xxSuccessful())
            .andReturn();

        Role updateRole = parseResponseToObject(Role.class, updateResult);
        assertTrue(updateRole.getPermissions().size() > 0, "Role expected to have new permission");
    }

    @Test
    void testCreateNewRole_andFailIfNotExists() throws Exception {
        mockMvc.perform(
            post(Endpoints.PERMISSIONS_V1 + "/roles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(SAMPLE_ROLE))
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
        )
        .andExpect(status().is2xxSuccessful());
    }

    @Test
    void testDeleteExistingRole() throws Exception {
        createSampleRole();

        mockMvc.perform(
            delete(Endpoints.PERMISSIONS_V1 + "/roles/" + SAMPLE_ROLE.getName())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
        )
        .andExpect(status().is2xxSuccessful());
    }


    void addPermissionToSampleRole() throws Exception {
        mockMvc.perform(
            put(Endpoints.PERMISSIONS_V1 + "/roles/" + SAMPLE_ROLE.getName() + "/add")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(SUPER_ADMIN_PERMISSION))
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
        );
    }
    
    void createSampleRole() throws Exception {
        mockMvc.perform(
            post(Endpoints.PERMISSIONS_V1 + "/roles")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(SAMPLE_ROLE))
                .header(AUTHORIZATION, composeAuthorization(getTokenResponse().getAccessToken()))
        );
    }

}

