package com.team.agility.lscore.apis;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.UpdateTokenExpirationDTO;
import com.team.agility.lscore.entities.TokenExpiration;
import com.team.agility.lscore.services.TokenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins={"*"})
@RestController
@RequestMapping(Endpoints.TOKEN_EXPIRATION_V1)
@RequiredArgsConstructor
@SecurityRequirement(name = Constants.SECURITY_REQUIREMENT)
@Tag(name = "JWT Token Expiration Configurator API", 
    description = "APIs for creating access, refresh token and token expirations")
public class TokenExpirationApi {
    
    private final TokenService tokenService;

    @Operation(summary = "Fetch all Token Expiration Configuration",
        description = "This endpoint is used to fetch all the token expiration configuration in the system")
    @GetMapping
    public ResponseEntity<List<TokenExpiration>> publicGetAllTokens() {
        return ResponseEntity.ok().body(tokenService.findAll());
    }

    @Operation(summary = "Update Token Expiration",
        description = "This endpoint is used to update the token expiration of a token configuration")
    @PutMapping("/{name}")
    public ResponseEntity<TokenExpiration> updateTokenExpiration(
        @PathVariable @NotBlank String name,
        @RequestBody @Valid UpdateTokenExpirationDTO payload) 
    {
        return ResponseEntity.ok().body(tokenService.update(name, payload));
    }
}
