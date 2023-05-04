package com.team.agility.lscore.services;

import java.util.List;

import com.team.agility.lscore.dtos.UpdateTokenExpirationDTO;
import com.team.agility.lscore.entities.TokenExpiration;

import java.util.Date;

public interface TokenService {
    TokenExpiration save(TokenExpiration tokenExpiration);
    TokenExpiration update(String name, UpdateTokenExpirationDTO payload);
    List<TokenExpiration> findAll();
    Date findAccessTokenExpirationDate();
    Date findRefreshTokenExpirationDate();
}
