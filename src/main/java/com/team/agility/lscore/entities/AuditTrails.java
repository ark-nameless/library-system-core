package com.team.agility.lscore.entities;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.agility.lscore.enums.AuditAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "audit_trails")
public class AuditTrails {
    @Id
    @GeneratedValue
    private Long id; 
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name="timestamp", nullable = false, updatable = false)
    @CreationTimestamp
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date timestamp;
}
