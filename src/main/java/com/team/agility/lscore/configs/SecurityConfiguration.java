package com.team.agility.lscore.configs;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.entities.Endpoint;
import com.team.agility.lscore.entities.Permission;
import com.team.agility.lscore.enums.EndpointSecurityEnum;
import com.team.agility.lscore.filters.JwtAuthenticationFilter;
import com.team.agility.lscore.services.PermissionService;
import com.team.agility.lscore.utilities.HttpUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final PermissionService permissionService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(Endpoints.SWAGGER_V3).permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        List<Endpoint> endpoints = permissionService.findAllEndpoints();

        endpoints.stream()
                .filter(e -> e.getSecurity() == EndpointSecurityEnum.SECURED)
                .forEachOrdered(e -> configureSecuredEndpoints(http, e));

        endpoints.stream()
                .filter(e -> e.getSecurity() == EndpointSecurityEnum.PUBLIC)
                .forEachOrdered(e -> configurePublicEndpoints(http, e));

        http.cors();
        http.headers().frameOptions().sameOrigin();
        http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        http.authorizeHttpRequests().anyRequest().authenticated().and().httpBasic();

        log.trace("running security chain");
        return http.build();
    }

    private void configureSecuredEndpoints(HttpSecurity http, Endpoint e) {
        List<String> permissions = e.getNeededPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());

        try {
            http.authorizeHttpRequests()
                    .requestMatchers(HttpUtils.toHttpMethod(e.getMethod()), e.getPath())
                    .hasAnyAuthority(permissions.toArray(new String[permissions.size()]));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void configurePublicEndpoints(HttpSecurity http, Endpoint e) {
        try {
            http.authorizeHttpRequests()
                    .requestMatchers(HttpUtils.toHttpMethod(e.getMethod()), e.getPath())
                    .permitAll();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
