package com.team.agility.lscore.repos;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.agility.lscore.entities.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);
}
