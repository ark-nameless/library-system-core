package com.team.agility.lscore.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.agility.lscore.entities.Endpoint;


public interface EndpointRepository extends JpaRepository<Endpoint, Long> {

    Optional<Endpoint> findByFullPath(String fullPath);
    List<Endpoint> findAllByMethod(String method);
    List<Endpoint> findByFullPathContaining(String substring);
    boolean existsByFullPath(String fullpath);
    
}

