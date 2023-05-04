package com.team.agility.lscore.services;

import java.util.List;

import com.team.agility.lscore.dtos.CreateNewBookBorrowDTO;
import com.team.agility.lscore.entities.BorrowBook;

public interface BookBorrowService {
    BorrowBook save(CreateNewBookBorrowDTO borrow);
    void remove(Long id);

    List<BorrowBook> findAll();
    BorrowBook findById(Long id);
    List<BorrowBook> findByBorrowerUsername(String username);

    boolean hasOutstandingBorrow(String username);
}
