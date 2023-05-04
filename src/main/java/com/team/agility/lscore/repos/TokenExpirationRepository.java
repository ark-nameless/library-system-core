package com.team.agility.lscore.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.agility.lscore.entities.TokenExpiration;

public interface TokenExpirationRepository extends JpaRepository<TokenExpiration, String>{
    
}
