package com.team.agility.lscore.apis;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.entities.Endpoint;
import com.team.agility.lscore.services.PermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins={"*"})
@RestController
@RequestMapping(Endpoints.ENDPOINTS_V1)
@SecurityRequirement(name = Constants.SECURITY_REQUIREMENT)
@Tag(name = "Endpoints API", description = "Endpoint management API")
public class EndpointsApi {

    @Autowired
    private PermissionService permissionService;

    @Operation(summary = "Public Get All Endpoints",
        description = "This endpoint is used to fetch all endpoints that users can connected to.",
        security = {})
    @GetMapping
    public ResponseEntity<List<Endpoint>> publicGetAllEndpoints() {
        return ResponseEntity.ok().body(permissionService.findAllEndpoints());    
    }

    @Operation(summary = "Get Endpoint with Full Path",
        description = "This endpoint is used to fetch an endpoint that have the full path stored in the system.")
    @GetMapping("/check")
    public ResponseEntity<Endpoint> checkingIfTheEndpointCanRetrivePrivileges() {
        return ResponseEntity.ok().body(permissionService.findEndpointWithFullPath("GET;/api/v1/health"));
    }

    @Operation(summary = "Fetch All Endpoints With",
        description = "This endpoint is used to fetch all endpoints that contains the endpoint specified that stored in the system.")
    @GetMapping("/{find}")
    public ResponseEntity<List<Endpoint>> getAllEndpointsThatContains(@PathVariable("find") String find) {
        return ResponseEntity.ok().body(permissionService.findEndpointWith(find));
    }
}

