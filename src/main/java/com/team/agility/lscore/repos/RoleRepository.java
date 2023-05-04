package com.team.agility.lscore.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.agility.lscore.entities.Role;

import java.util.Optional;


public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
}