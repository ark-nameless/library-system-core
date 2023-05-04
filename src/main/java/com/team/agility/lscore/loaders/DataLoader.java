package com.team.agility.lscore.loaders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.entities.Endpoint;
import com.team.agility.lscore.entities.Permission;
import com.team.agility.lscore.entities.Role;
import com.team.agility.lscore.entities.TokenExpiration;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.enums.EndpointSecurityEnum;
import com.team.agility.lscore.repos.UserRepository;
import com.team.agility.lscore.services.PermissionService;
import com.team.agility.lscore.services.TokenService;
import com.team.agility.lscore.services.UserService;

import jakarta.annotation.PostConstruct;

@Component
public class DataLoader {

    @Autowired
    private TokenService tokenService;

    @Autowired 
    private PermissionService permissionService;

    @Autowired
    private UserService userService;

    @Autowired
    RequestMappingHandlerMapping requestMappingHandlerMapping;


    @PostConstruct
    public void loadEndpoints() {
        Set<Permission> adminPermissions = new HashSet<>();
        Permission globalPermission = createPrivilegeIfNotFound("PERMISSION_TO_ACCESS_ALL");
        adminPermissions.add(globalPermission);

        requestMappingHandlerMapping.getHandlerMethods().forEach((key,value) -> {
            final String endpoinMethod = value.getMethod().toString();
            if (endpoinMethod.contains("com.team.agility.lscore")) {

                // Get the endpoint method name and convert it to a privilege
                String endpointAssocPrivilege = Arrays
                        .stream(value.getMethod().getName().split("(?=[A-Z])"))
                        .map(String::toLowerCase)
                        .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                            list.set(0, capitalizeFirstLetter(list.get(0)));
                            return String.join("_", list);
                        }))
                        .toUpperCase();

                EndpointSecurityEnum pathSecurity = endpointAssocPrivilege.contains("PUBLIC") ? EndpointSecurityEnum.PUBLIC : EndpointSecurityEnum.SECURED;
                Permission endpointPermission = new Permission();
                if (pathSecurity == EndpointSecurityEnum.SECURED) {
                    endpointPermission = createPrivilegeIfNotFound(endpointAssocPrivilege);
                    adminPermissions.add(endpointPermission);
                }

                final String method = key.getMethodsCondition().getMethods().stream().findFirst().get().toString();
                final String path = key.getPatternValues().stream().findFirst().get();
                Endpoint saveEndpoint = Endpoint.builder()
                        .method(method)
                        .path(path)
                        .fullPath(method + ";" + path)
                        .security(pathSecurity)
                        .build();
                
                saveEndpoint = permissionService.saveEndpoint(saveEndpoint);

                if (pathSecurity == EndpointSecurityEnum.SECURED) {
                    permissionService.addPermissionToEndpoint(globalPermission.getId(), saveEndpoint.getId());
                    permissionService.addPermissionToEndpoint(endpointPermission.getId(), saveEndpoint.getId());
                }
            }
        });

        Role adminRole = Role.builder().name("SUPER_ADMIN").build();
        adminRole.getPermissions().addAll(adminPermissions);
        adminRole = permissionService.saveRole(adminRole);

        Set<Role> adminRoles = new HashSet<>();
        adminRoles.add(adminRole);

        User adminUser = User.builder()
            .email("admin@email.com")
            .username("admin")
            .password("admin")
            .build();
        adminUser.getRoles().add(adminRole);
        userService.save(adminUser);
    }


    @PostConstruct 
    public void loadTokenConfigurations() {
        TokenExpiration accessToken = TokenExpiration.builder()
            .name(Constants.ACCESS_TOKEN)
            .days(0)
            .hours(0)
            .minutes(15)
            .build();
        TokenExpiration refreshToken = TokenExpiration.builder()
            .name(Constants.REFRESH_TOKEN)
            .days(7)
            .hours(0)
            .minutes(0)
            .build();

        tokenService.save(accessToken);
        tokenService.save(refreshToken);
    }


    
    private Permission createPrivilegeIfNotFound(String permissionName) {
        Permission newPermission = Permission.builder().name(permissionName).build();
        newPermission = permissionService.savePermission(newPermission);

        return newPermission;
    }


    private static String capitalizeFirstLetter(String word) {
        return "PERMISSION_TO_" + Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }
}
