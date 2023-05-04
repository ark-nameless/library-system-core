package com.team.agility.lscore.services;

import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.team.agility.lscore.entities.Book;

public interface BookService {
    Book save(Book book);
    void remove(String isbn);
    Book update(Book updateBook);
    List<Book> saveWithExcel(Sheet sheet);
    
    List<Book> findAll();
    Book findByIsbn(String isbn);
    Page<Book> findBySearchCriteria(Specification<Book> spec, Pageable page);

    boolean existByIsbn(String isbn);
}
