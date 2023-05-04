package com.team.agility.lscore.services;

import java.time.LocalTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.agility.lscore.entities.AuditTrails;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.enums.AuditAction;
import com.team.agility.lscore.repos.AuditTrailsRepository;
import com.team.agility.lscore.repos.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Transactional
@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditTrailsRepository auditRepo;
    private final UserRepository userRepo;
    private final ObjectMapper objectMapper;
    
    private User userLogger;

    public List<AuditTrails> findAll() {
        return auditRepo.findAll();
    }

    public void log(AuditAction action, String message) {
        getUser();
        auditRepo.save(createLog(action, message));
    }

    public void log(String username, AuditAction action, String message) {
        getUser(username);
        auditRepo.save(createLog(action, message));
    }

    public void log(AuditAction action, Object object) {
        String message = getMessage(object);
        getUser();
        auditRepo.save(createLog(action, message));
    }

    public void log(String username, AuditAction action, Object object) {
        String message = getMessage(object);
        getUser(username);
        auditRepo.save(createLog(action, message));
    }

    private void getUser(String username) {
        userLogger = userRepo.findByUsername(username).orElse(null);
    }

    private void getUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            userLogger = userRepo.findByEmail(authentication.getName()).orElse(null);
        } catch (Exception e) {
            userLogger = null;
        }
    }

    private String getMessage(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
    

    private AuditTrails createLog(AuditAction action, String message) {
        AuditTrails log = AuditTrails.builder()
                            .action(action)
                            .details(message)
                            .user(userLogger)
                            .build();
        return log;
    }

}