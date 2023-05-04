package com.team.agility.lscore.repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.agility.lscore.entities.User;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}

