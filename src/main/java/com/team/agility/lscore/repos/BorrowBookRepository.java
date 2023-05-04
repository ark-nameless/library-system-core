package com.team.agility.lscore.repos;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team.agility.lscore.entities.BorrowBook;

public interface BorrowBookRepository extends JpaRepository<BorrowBook, Long> {
    List<BorrowBook> findAllByBorrowerUsername(String username);
}
