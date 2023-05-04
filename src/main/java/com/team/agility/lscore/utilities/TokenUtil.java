package com.team.agility.lscore.utilities;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.context.SpringContext;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.exceptions.AuthorizationException;
import com.team.agility.lscore.services.TokenService;
import com.team.agility.lscore.services.TokenServiceImpl;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TokenUtil {
    private final TokenService tokenService =  SpringContext.getBean(TokenServiceImpl.class);

    public Algorithm algorithm() {
        return Algorithm.HMAC256("aRk_nAmELeSs".getBytes());
    }

    public String parseToken(String authorizationHeader) {
        return authorizationHeader.substring(Constants.TOKEN_TYPE.concat(" ").length());
    }

    public String parseUsername(String token) {
        try {
            final JWTVerifier verifier = JWT.require(TokenUtil.algorithm()).build();
            final DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getSubject();
        } catch (Exception e) {
            System.out.println("token expired");
            throw new AuthorizationException("Token has expired");
        }
    }

    public String[] parseRoles(String token) {
        try {
            final JWTVerifier verifier = JWT.require(TokenUtil.algorithm()).build();
            final DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim(Constants.JWT_CLAIM_PERMISSIONS).asArray(String.class);
        } catch (Exception e) {
            System.out.println("token expired");
            throw new AuthorizationException("Token has expired");
        }
    }

    public String generateAccessToken(User user) {
        final Date accessTokenExpirationDate = tokenService.findAccessTokenExpirationDate();
        List<String> authorities = new ArrayList<>();
        user.getRoles()
            .forEach(r -> r.getPermissions()
                .forEach(p -> authorities.add(p.getName())));

        return JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(accessTokenExpirationDate)
            .withIssuer(Constants.REQUEST_URI)
            .withClaim(Constants.JWT_CLAIM_PERMISSIONS, authorities)
            .withClaim(Constants.JWT_CLAIM_ROLES, user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toList()))
            .sign(TokenUtil.algorithm());
    }

    public String generateAccessToken(org.springframework.security.core.userdetails.User user) {
        Date accessTokenExpirationDate = tokenService.findAccessTokenExpirationDate();
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(accessTokenExpirationDate)
                .withIssuer(Constants.REQUEST_URI)
                .withClaim(Constants.JWT_CLAIM_PERMISSIONS,
                        user.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .sign(TokenUtil.algorithm());
    }

    public String generateRefreshToken(org.springframework.security.core.userdetails.User user) {
        final Date refreshTokenExpirationDate = tokenService.findRefreshTokenExpirationDate();
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(refreshTokenExpirationDate)
                .withIssuer(Constants.REQUEST_URI)
                .sign(TokenUtil.algorithm());
    }

    public String generateRefreshToken(User user) {
        final Date refreshTokenExpirationDate = tokenService.findRefreshTokenExpirationDate();
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(refreshTokenExpirationDate)
                .withIssuer(Constants.REQUEST_URI)
                .sign(TokenUtil.algorithm());
    }

    public boolean isExpired(String token) {
        final DecodedJWT decoded; 
        try {
            final JWTVerifier verifier = JWT.require(TokenUtil.algorithm()).build();
            decoded = verifier.verify(token);
            return decoded.getExpiresAt().before(new Date());
        } catch (JWTVerificationException exception) {
            return false;
        }
    }
    
    public boolean hasClaimPresence(String token, String claim) {
        try {
            JWTVerifier verifier = JWT.require(TokenUtil.algorithm())
                .withClaimPresence(claim).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }
}
