package com.team.agility.lscore.apis;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.AddPermissionToRoleDTO;
import com.team.agility.lscore.dtos.CreateNewRoleDTO;
import com.team.agility.lscore.dtos.RemovePermissionOnRoleDTO;
import com.team.agility.lscore.entities.Permission;
import com.team.agility.lscore.entities.Role;
import com.team.agility.lscore.services.PermissionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins={"*"})
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoints.PERMISSIONS_V1)
@SecurityRequirement(name = Constants.SECURITY_REQUIREMENT)
@Tag(name = "Permission's API", description = "Permissions management API")
public class PermissionsApi {

    private final PermissionService permissionService;

    @Operation(summary = "Get All Privileges",
        description = "This endpoint is used to fetch all permissions that is store in the system")
    @GetMapping("/privileges")
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok().body(permissionService.findAllPermissions());
    }
    
    @Operation(summary = "Get a Privilege",
        description = "This endpoint is used to fetch a permissions that is store in the system using the privilege name")
    @GetMapping("/privilege/{name}")
    public ResponseEntity<Permission> getPrivilegeByName(
        @PathVariable @NotBlank String name
    ) {
        return ResponseEntity.ok().body(permissionService.findPermissionByName(name));
    }

    // ROLES

    @Operation(summary = "Get all Roles",
        description = "This endpoint is used to fetch all roles that is store in the system.")
    @GetMapping("/roles")
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok().body(permissionService.findAllRoles());
    }

    @Operation(summary = "Get a Role", 
        description = "This endpoint id used to fetch a specific role by name that is store in the database.")
    @GetMapping("/roles/{roleName}")
    public ResponseEntity<Role> getRoleByName(
        @PathVariable @NotBlank String roleName
    ) {
        return ResponseEntity.ok().body(permissionService.findRoleByName(roleName));
    }

    @Operation(summary = "Create new Role",
        description = "This endpoint is used to create a new ")
    @PostMapping("/roles")
    public ResponseEntity<Role> createNewRole(
        @RequestBody @Valid CreateNewRoleDTO payload
    ) {
        Role role = Role.builder().name(payload.getName().trim()).build();
        return ResponseEntity.ok().body(permissionService.saveRole(role));
    }

    @Operation(summary = "Add New Privilege to Role",
        description = "This endpoint is used to add new permission to an existing role that is store in the system")
    @PutMapping("/roles/{roleName}/add")
    public ResponseEntity<Role> addNewPermissionToRole(
        @PathVariable @NotBlank String roleName,
        @RequestBody @Valid AddPermissionToRoleDTO permission
    ) {
        return ResponseEntity.ok().body(permissionService.addPermissionToRole(permission.getName().trim(), roleName));
    }

    @Operation(summary = "Remove Privilege to Role",
        description = "This endpoint is used to remove a permission to an existing role that is store in the system")
    @PutMapping("/roles/{roleName}/remove")
    public ResponseEntity<Role> removePrivilegeOnRole(
        @PathVariable @NotBlank String roleName,
        @RequestBody @Valid RemovePermissionOnRoleDTO permission
    ) {
        return ResponseEntity.ok().body(permissionService.removePermissionOnRole(permission.getName(), roleName));
    }

    @Operation(summary = "Remove Role",
        description = "This endpoint is used to remove an already existing role using the role name")
    @DeleteMapping("/roles/{roleName}")
    public ResponseEntity<?> removeExistingRole(
        @PathVariable @NotBlank String roleName
    ) {
        permissionService.removeRoleByName(roleName);
        return ResponseEntity.ok().build();
    }

}
