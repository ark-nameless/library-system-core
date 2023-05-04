package com.team.agility.lscore.services;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.team.agility.lscore.dtos.CreateNewBookBorrowDTO;
import com.team.agility.lscore.entities.Book;
import com.team.agility.lscore.entities.BorrowBook;
import com.team.agility.lscore.entities.User;
import com.team.agility.lscore.enums.AuditAction;
import com.team.agility.lscore.exceptions.BadRequestException;
import com.team.agility.lscore.exceptions.DuplicateRecordException;
import com.team.agility.lscore.exceptions.ResourceNotFoundException;
import com.team.agility.lscore.models.BookBorrowDetails;
import com.team.agility.lscore.repos.BookRepository;
import com.team.agility.lscore.repos.BorrowBookRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Transactional
@Service
public class BookBorrowServiceImpl implements BookBorrowService {
    private final BorrowBookRepository borrowRepo;
    private final UserService userService;
    private final BookService bookService;
    private final AuditLogService auditLogService;


    @Override
    public BorrowBook save(CreateNewBookBorrowDTO borrow) {
        Set<BookBorrowDetails> borrowBooks = new HashSet<>();

        borrow.getListOfBooks().forEach(book -> {
            if (!bookService.existByIsbn(book.getIsbn())) {
                throw new ResourceNotFoundException("Book ISBN not found");
            }

            BookBorrowDetails details = new BookBorrowDetails();
            Date returnDate = new Date();
            try {
                returnDate = new SimpleDateFormat("yyyy-MM-dd").parse(book.getReturnDate());
            } catch (Exception e) {
            }
            if (returnDate.before(new Date())) {
                throw new BadRequestException("Invalid Return Date on book borrowed");
            }
            details.setIsbn(book.getIsbn());
            details.setReturnDate(returnDate);

            borrowBooks.add(details);
        });

        User borrower = userService.findByUsername(borrow.getBorrower());
        User issuer = userService.findByUsername(borrow.getIssuer());
        BorrowBook borrowDetail = BorrowBook.builder()
                                    .issuerId(issuer.getId())
                                    .borrower(borrower)
                                    .borrowedBook(borrowBooks)
                                    .build();
        auditLogService.log(AuditAction.INSERT, borrowDetail);
        return borrowRepo.save(borrowDetail);
    }

    @Override
    public void remove(Long id) {
        BorrowBook borrowBook = findById(id);
        auditLogService.log(AuditAction.DELETE, borrowBook);
        borrowRepo.delete(borrowBook);
    }

    @Override
    public List<BorrowBook> findAll() {
        return borrowRepo.findAll();
    }

    @Override
    public List<BorrowBook> findByBorrowerUsername(String username) {
        return borrowRepo.findAllByBorrowerUsername(username);
    }

    @Override
    public BorrowBook findById(Long id) {
        return borrowRepo.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Book Borrow Details not found.")
        );
    }

    @Override
    public boolean hasOutstandingBorrow(String username) {
        for (BorrowBook borrow: findByBorrowerUsername(username)) {
            if (!borrow.getIsReturned()) return true;
        }
        return false;
    }

    
}
