package com.team.agility.lscore.repos;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.team.agility.lscore.entities.Book;


public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book>{
    Optional<Book> findByIsbn(String isbn);
    Optional<Book> findByAuthor(String author);
    List<Book> findByTitleContains(String title);
    List<Book> findByIsbnContains(String isbn);
    List<Book> findByAuthorContains(String author);
    List<Book> findByDescriptionContains(String description);

    boolean existsByIsbn(String isbn);

    List<Book> findAllByPublicationDateBetween(
        Date publicationDateStart,
        Date publicationDateEnd
    );
}
