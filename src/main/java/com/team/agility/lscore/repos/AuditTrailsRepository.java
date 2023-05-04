package com.team.agility.lscore.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.agility.lscore.entities.AuditTrails;

public interface AuditTrailsRepository extends JpaRepository<AuditTrails, Long> {
    
}
