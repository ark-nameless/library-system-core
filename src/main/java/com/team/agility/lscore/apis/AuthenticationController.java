package com.team.agility.lscore.apis;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.agility.lscore.annotations.CreatedUserResponseAnnotation;
import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.AuthenticationRequest;
import com.team.agility.lscore.dtos.ChangeUserPasswordDTO;
import com.team.agility.lscore.dtos.CreateNewUserDTO;
import com.team.agility.lscore.dtos.TokenResponse;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.exceptions.AuthorizationException;
import com.team.agility.lscore.exceptions.InvalidCredentialsException;
import com.team.agility.lscore.services.AuthenticationService;
import com.team.agility.lscore.services.UserService;
import com.team.agility.lscore.utilities.TokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins={"*"})
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoints.AUTH_V1)
@CreatedUserResponseAnnotation
@Tag(name = "Authentication API", description = "Authentication management API")
public class AuthenticationController {

    private final AuthenticationService authService;
    private final UserService userService;
    
    @Operation(summary = "Register new User",
        description = "This endpoint is used to create a new user login information")
    @PostMapping("/register")
    public ResponseEntity<?> publicRegisterNewUser(
        HttpServletRequest request,
        @RequestBody CreateNewUserDTO payload
    ) {
        return ResponseEntity.created(URI.create(request.getRequestURI()))
            .body(authService.register(payload));
    }

    @Operation(summary = "Change user's password",
        description = "This endpoint is used to change user's password given that the last password is rememebered")
    @PutMapping("/{username}/password")
    public ResponseEntity<?> publicUserChangePassword(
        @PathVariable String username,
        @RequestBody @Valid ChangeUserPasswordDTO payload
    ) {
        userService.changePassword(username, payload);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Login User",
        description = "This endpoint is used to login user and get tokens to used different endpoints")
    @PostMapping("/authenticate")
    public ResponseEntity<TokenResponse> publicLoginUser(
        @RequestBody AuthenticationRequest request
    ) throws InvalidCredentialsException {
        try {
            TokenResponse result = authService.authenticate(request);

            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            userService.incrementInvalidAttempt(request.getUsername());
            throw new InvalidCredentialsException(e.getMessage());
        }
    }

    @Operation(summary = "Refresh token",
            description = "This endpoint is used for getting an access & refresh token by providing refresh token")
    @SecurityRequirement(name = Constants.SECURITY_REQUIREMENT)
    @GetMapping("/refresh-token")
    public ResponseEntity<TokenResponse> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response)
            throws AuthorizationException {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            throw new AuthorizationException("Refresh Token Missing");
        }

        if (authorizationHeader.startsWith(Constants.TOKEN_TYPE.concat(" "))) {
            try {
                String refreshToken = TokenUtil.parseToken(authorizationHeader);
                String username = TokenUtil.parseUsername(refreshToken);
                User user = userService.findByUsername(username);
                String accessToken = TokenUtil.generateAccessToken(user);

                return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
            } catch (Exception exception) {
                throw new AuthorizationException(exception.getMessage());
            }
        } else {
            throw new AuthorizationException("Invalid Token Type");
        }
    }
}
