package com.team.agility.lscore.services;

import java.util.List;

import com.team.agility.lscore.entities.Endpoint;
import com.team.agility.lscore.entities.Permission;
import com.team.agility.lscore.entities.Role;


public interface PermissionService {
    
    Endpoint saveEndpoint(Endpoint endpoint);
    Endpoint findEndpointWithFullPath(String fullPath);
    List<Endpoint> findAllEndpoints();
    List<Endpoint> findAllEndpointsWithMethod(String method);
    List<Endpoint> findEndpointWith(String substring);
    Endpoint addPermissionToEndpoint(String permissionName, Long endpointId);
    Endpoint addPermissionToEndpoint(Long permissionId, Long endpointId);
    void removePrivilegeInEndpoint(String permissionName, Long endpointId);
    void removePrivilegeInEndpoint(Long permissionId, Long endpointId);
    boolean existsEndpointByFullPath(String fullpath);

    Permission savePermission(Permission permission);
    Permission findPermissionByName(String name);
    List<Permission> findAllPermissions();
    void removePermission(String name);
    boolean existsPermissionByName(String name);

    Role saveRole(Role role);
    Role findRoleByName(String name);
    List<Role> findAllRoles();
    void removeRoleByName(String name);
    Role addPermissionToRole(String permissionName, String roleName);
    Role removePermissionOnRole(String permissionName, String roleName);
    boolean existsRoleByName(String name);

}
