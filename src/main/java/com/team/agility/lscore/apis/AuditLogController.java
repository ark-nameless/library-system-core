package com.team.agility.lscore.apis;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.entities.AuditTrails;
import com.team.agility.lscore.services.AuditLogService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins={"*"})
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoints.LOGS_V1)
@SecurityRequirement(name = Constants.SECURITY_REQUIREMENT)
@Tag(name = "Audit Log API", description = "Endpoint for getting all logs")
public class AuditLogController {
    private final AuditLogService auditLogService;

    @Operation(summary = "Get All Logs",
        description = "This endpoint is used to fetch all logs"
    )
    @GetMapping
    public ResponseEntity<List<AuditTrails>> hello() {
        return ResponseEntity.ok().body(auditLogService.findAll());
    }
}
