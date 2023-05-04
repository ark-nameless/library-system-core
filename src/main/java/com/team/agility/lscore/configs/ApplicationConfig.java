package com.team.agility.lscore.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.models.AppUserDetails;
import com.team.agility.lscore.repos.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepo;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                log.trace("running User Detail Service; {}", username);
                User user = userRepo.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found."));
                AppUserDetails userDetails = AppUserDetails.build(user);
                return userDetails;
            }
            
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
