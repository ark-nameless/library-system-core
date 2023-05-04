package com.team.agility.lscore.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.dtos.UpdateTokenExpirationDTO;
import com.team.agility.lscore.entities.TokenExpiration;
import com.team.agility.lscore.enums.AuditAction;
import com.team.agility.lscore.exceptions.ResourceNotFoundException;
import com.team.agility.lscore.repos.TokenExpirationRepository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Transactional
@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenExpirationRepository tokenExpirationRepo;

    @Autowired 
    private AuditLogService auditLogService;

    @Override
    public TokenExpiration save(TokenExpiration tokenExpiration) {
        log.trace("Saving new Token Expiration: {}", tokenExpiration);
        Optional<TokenExpiration> foundToken = tokenExpirationRepo.findById(tokenExpiration.getName());
        if (foundToken.isPresent()){
            return foundToken.get();
        }
        
        auditLogService.log(AuditAction.INSERT, tokenExpiration);
        return tokenExpirationRepo.save(tokenExpiration);
    }

    @Override
    public TokenExpiration update(String name, UpdateTokenExpirationDTO payload) {
        log.trace("Updating an existing token: {}", payload);
        TokenExpiration currentTokenExp = tokenExpirationRepo.findById(name)
            .orElseThrow();
        currentTokenExp.setDays(payload.getDays());
        currentTokenExp.setHours(payload.getHours());
        currentTokenExp.setMinutes(payload.getMinutes());

        auditLogService.log(AuditAction.UPDATE, currentTokenExp);
        return currentTokenExp;
    }

    @Override
    public List<TokenExpiration> findAll() {
        log.trace("Fetching all Token Expiration.");
        return tokenExpirationRepo.findAll();
    }

    @Override
    public Date findAccessTokenExpirationDate() {
        TokenExpiration tokenExpiration = tokenExpirationRepo.findById(Constants.ACCESS_TOKEN)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Access Token Expiration details not found"));
        return getDateExpiration(tokenExpiration);
    }

    @Override
    public Date findRefreshTokenExpirationDate() {
        TokenExpiration tokenExpiration = tokenExpirationRepo.findById(Constants.REFRESH_TOKEN)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Refresh Token Expiration details not found"));
        return getDateExpiration(tokenExpiration);
    }

    private Date getDateExpiration(TokenExpiration tokenExpiration) {
        long days = TimeUnit.DAYS.toMillis(tokenExpiration.getDays());
        long hours = TimeUnit.HOURS.toMillis(tokenExpiration.getHours());
        long minutes = TimeUnit.MINUTES.toMillis(tokenExpiration.getMinutes());

        return new Date(System.currentTimeMillis() + (days + hours + minutes));
    }
    
}
