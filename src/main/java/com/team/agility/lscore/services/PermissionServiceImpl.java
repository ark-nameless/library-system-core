package com.team.agility.lscore.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.team.agility.lscore.entities.Endpoint;
import com.team.agility.lscore.entities.Permission;
import com.team.agility.lscore.entities.Role;
import com.team.agility.lscore.enums.AuditAction;
import com.team.agility.lscore.exceptions.ResourceNotFoundException;
import com.team.agility.lscore.repos.EndpointRepository;
import com.team.agility.lscore.repos.PermissionRepository;
import com.team.agility.lscore.repos.RoleRepository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;


@Log4j2
@Transactional
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepo;

    @Autowired
    private EndpointRepository endpointRepo;

    @Autowired 
    private RoleRepository roleRepo;

    @Lazy
    @Autowired
    private AuditLogService auditLogService;


    @Override
    public Endpoint saveEndpoint(Endpoint endpoint) {
        log.trace("Adding new endpoint: {}", endpoint);
        if (existsEndpointByFullPath(endpoint.getFullPath())) {
            return endpointRepo.findByFullPath(endpoint.getFullPath()).orElseThrow(() -> new ResourceNotFoundException());
        }
        auditLogService.log(AuditAction.INSERT, endpoint);
        return endpointRepo.save(endpoint);
    }

    @Override
    public Endpoint findEndpointWithFullPath(String fullPath) {
        log.trace("Fetching Endpoints with full path: {}", fullPath);
        return endpointRepo.findByFullPath(fullPath)
            .orElseThrow(() -> new ResourceNotFoundException("Unable to find any Endpoints with full path: " + fullPath));
    }

    @Override
    public List<Endpoint> findAllEndpoints() {
        log.trace("Fetching all endpoints");
        return endpointRepo.findAll();
    }

    @Override
    public List<Endpoint> findAllEndpointsWithMethod(String method) {
        log.trace("Fetching all endpoints with medthod: {}", method);
        return endpointRepo.findAllByMethod(method);
    }

    @Override
    public List<Endpoint> findEndpointWith(String substring) {
        log.trace("Fetching all endpoints with: {}", substring);
        return endpointRepo.findByFullPathContaining(substring);
    }

    @Override
    public Endpoint addPermissionToEndpoint(String permissionName, Long endpointId) {
        Endpoint endpoint = endpointRepo.findById(endpointId)
            .orElseThrow(() -> new ResourceNotFoundException("No endpoint found with id provided."));
        Permission permission = permissionRepo.findByName(permissionName)
            .orElseThrow(() -> new ResourceNotFoundException("No permission found with name provided."));

        log.trace("Adding new permission: {} to endpoint: {}", permission, endpoint);
        Set<Permission> permissions = endpoint.getNeededPermissions();
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
        auditLogService.log(AuditAction.UPDATE, endpoint);
        return endpointRepo.save(endpoint);
    }

    @Override
    public Endpoint addPermissionToEndpoint(Long permissionId, Long endpointId) {
        Endpoint endpoint = endpointRepo.findById(endpointId)
            .orElseThrow(() -> new ResourceNotFoundException("No endpoint found with id provided."));
        Permission permission = permissionRepo.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("No permission found with id provided."));
            
        log.trace("Adding new permission: {} to endpoint: {}", permission, endpoint);
        Set<Permission> permissions = endpoint.getNeededPermissions();
        if (!permissions.contains(permission)) {
            permissions.add(permission);
        }
        auditLogService.log(AuditAction.UPDATE, endpoint);
        return endpointRepo.save(endpoint);
    }

    @Override
    public void removePrivilegeInEndpoint(String permissionName, Long endpointId) {
        Endpoint endpoint = endpointRepo.findById(endpointId)
            .orElseThrow(() -> new ResourceNotFoundException("No endpoint found with id provided."));
        Permission permission = permissionRepo.findByName(permissionName)
            .orElseThrow(() -> new ResourceNotFoundException("No permission found with name provided."));
        
        log.trace("Removing permission: {} in endpoint: {}", permission, endpoint);
        endpoint.getNeededPermissions().remove(permission);
        auditLogService.log(AuditAction.UPDATE, endpoint);
        endpointRepo.save(endpoint);
    }

    @Override
    public void removePrivilegeInEndpoint(Long permissionId, Long endpointId) {
        Endpoint endpoint = endpointRepo.findById(endpointId)
            .orElseThrow(() -> new ResourceNotFoundException("No endpoint found with id provided."));
        Permission permission = permissionRepo.findById(permissionId)
            .orElseThrow(() -> new ResourceNotFoundException("No permission found with id provided."));
        
        log.trace("Removing permission: {} in endpoint: {}", permission, endpoint);
        endpoint.getNeededPermissions().remove(permission);
        auditLogService.log(AuditAction.UPDATE, endpoint);
        endpointRepo.save(endpoint);
    }

    @Override
    public boolean existsEndpointByFullPath(String fullpath) {
        log.trace("Checking if {} exists", fullpath);
        return endpointRepo.existsByFullPath(fullpath);
    }

    @Override
    public Permission savePermission(Permission permission) {
        log.trace("Adding new permission: {}", permission);
        if (existsPermissionByName(permission.getName())) {
            return findPermissionByName(permission.getName());
        }
        auditLogService.log(AuditAction.INSERT, permission);
        return permissionRepo.save(permission);
    }

    @Override
    public Permission findPermissionByName(String name) {
        log.trace("Fetching permission: {}", name);
        return permissionRepo.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("No permission found with the provided name"));
    }

    @Override
    public List<Permission> findAllPermissions() {
        log.trace("Fetching all permissions");
        return permissionRepo.findAll();
    }

    @Override
    public void removePermission(String name) {
        log.trace("Removing permission: {}", name);
        Permission permission = permissionRepo.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("No privilege found with the provided name"));
        auditLogService.log(AuditAction.DELETE, permission);
        permissionRepo.delete(permission);
    }

    @Override
    public boolean existsPermissionByName(String name) {
        log.trace("Checking if {} exists", name);
        return permissionRepo.existsByName(name);
    }

    @Override
    public Role saveRole(Role role) {
        log.trace("Adding new role: {}", role);
        if (existsRoleByName(role.getName())) {
            return findRoleByName(role.getName());
        }
        auditLogService.log(AuditAction.INSERT, role);
        return roleRepo.save(role);
    }

    @Override
    public Role findRoleByName(String name) {
        log.trace("Fetching role: {}", name);
        return roleRepo.findByName(name)
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
    }

    @Override
    public List<Role> findAllRoles() {
        log.trace("Fetching all roles");
        return roleRepo.findAll();
    }

    @Override
    public void removeRoleByName(String name) {
        Role role = findRoleByName(name);
        log.trace("Deleting role: {}", role);
        auditLogService.log(AuditAction.DELETE, role);
        roleRepo.delete(role);
    }

    @Override
    public Role addPermissionToRole(String permissionName, String roleName) {
        Role role = findRoleByName(roleName);
        Permission permission = findPermissionByName(permissionName);

        role.getPermissions().add(permission);
        auditLogService.log(AuditAction.UPDATE, role);
        return role;
    }

    @Override
    public Role removePermissionOnRole(String permissionName, String roleName) {
        Role role = findRoleByName(roleName);
        Permission permission = findPermissionByName(permissionName);

        role.getPermissions().remove(permission);
        auditLogService.log(AuditAction.UPDATE, role);
        return role;
    }
    
    @Override
    public boolean existsRoleByName(String name) {
        log.trace("Checking if {} exists", name);
        return roleRepo.existsByName(name);
    }
    
}
