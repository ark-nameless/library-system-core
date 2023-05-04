package com.team.agility.lscore.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.team.agility.lscore.dtos.AuthenticationRequest;
import com.team.agility.lscore.dtos.CreateNewUserDTO;
import com.team.agility.lscore.dtos.TokenResponse;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.enums.AuditAction;
import com.team.agility.lscore.exceptions.DuplicateRecordException;
import com.team.agility.lscore.models.AppUserDetails;
import com.team.agility.lscore.utilities.TokenUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final AuditLogService auditLogService;

    public User register(CreateNewUserDTO request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new DuplicateRecordException("User with same email already exists");
        }
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .build();

        log.trace("User registered: {}", user);
        auditLogService.log(AuditAction.INSERT, user);
        return userService.save(user);
    }

    public TokenResponse authenticate(AuthenticationRequest request) {
        var user = userService.findByUsername(request.getUsername());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword()));

        var userDetails = AppUserDetails.build(user);
        var accessToken = TokenUtil.generateAccessToken(user);
        var refreshToken = TokenUtil.generateRefreshToken(user);

        log.trace("User login token: {}", userDetails);
        userService.resetInvalidAttempt(user.getUsername());
        return TokenResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
    }
    
}
