package com.team.agility.lscore.entities;


import java.util.Set;

import com.team.agility.lscore.enums.EndpointSecurityEnum;

import java.util.HashSet;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
@Table(name = "endpoints")
public class Endpoint {
    @Id
    @GeneratedValue
    private Long id; 

    @Column
    private String method;

    @Column(nullable = false)
    private String path;

    @Column(name = "full_path", unique = true, nullable = false)
    private String fullPath;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EndpointSecurityEnum security;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "endpoints_permissions",
        joinColumns = @JoinColumn(name = "endpoints_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name = "permissions_id", referencedColumnName = "id")
    )
    private final Set<Permission> neededPermissions = new HashSet<>();
}