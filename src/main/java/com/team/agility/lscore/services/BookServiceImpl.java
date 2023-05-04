package com.team.agility.lscore.services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.team.agility.lscore.entities.Book;
import com.team.agility.lscore.enums.AuditAction;
import com.team.agility.lscore.exceptions.DuplicateRecordException;
import com.team.agility.lscore.exceptions.ResourceNotFoundException;
import com.team.agility.lscore.repos.BookRepository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Transactional
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepo;
    
    @Autowired
    private AuditLogService auditLogService;

    @Override
    public Book save(Book book) {
        String isbn = book.getIsbn();
        book.setIsbn(isbn.replaceAll("[^\\d.]", ""));
        
        log.trace("Adding new book: {}", book);
        if (existByIsbn(book.getIsbn())) {
            throw new DuplicateRecordException("Book with same ISBN already exists");
        }

        auditLogService.log(AuditAction.INSERT, book);

        return bookRepo.save(book);
    }

    @Override 
    public List<Book> saveWithExcel(Sheet sheet) {
        List<Book> registeredBooks = new ArrayList<>();
        List<Book> errorBooks = new ArrayList<>();

        DataFormatter dataFormatter = new DataFormatter();

        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Book book = new Book();
            
            try {
                String isbn = dataFormatter.formatCellValue(row.getCell(0));
                String title = dataFormatter.formatCellValue(row.getCell(1));
                String author = dataFormatter.formatCellValue(row.getCell(2));
                String description = dataFormatter.formatCellValue(row.getCell(3));
                Date publicationDate = new SimpleDateFormat("yyyy-MM-dd")
                    .parse(dataFormatter.formatCellValue(row.getCell(4)));

                book = Book.builder()
                    .isbn(isbn)
                    .title(title)
                    .author(author)
                    .description(description)
                    .publicationDate(publicationDate)
                    .build();

                save(book);
            } catch (Exception e) {
                errorBooks.add(book);
            }
        }

        auditLogService.log(AuditAction.INSERT, registeredBooks);
        return errorBooks;
    }

    @Override
    public void remove(String isbn) {
        log.trace("Remove book with ISBN: {}", isbn);
        if (!existByIsbn(isbn)){
            throw new ResourceNotFoundException("Book not found");
        }
        Book removeBook = bookRepo.findByIsbn(isbn).orElseThrow(() 
            -> new ResourceNotFoundException("Book not found"));
        auditLogService.log(AuditAction.DELETE, removeBook);
        bookRepo.delete(removeBook);
    }

    @Override
    public Book update(Book updateBook) {
        log.trace("Updating book: {}", updateBook);
        Book book = bookRepo.findByIsbn(updateBook.getIsbn()).orElseThrow(() 
            -> new ResourceNotFoundException("Book not found"));
            
        book.setTitle(updateBook.getTitle());
        book.setAuthor(updateBook.getAuthor());
        book.setDescription(updateBook.getDescription());
        book.setPublicationDate(updateBook.getPublicationDate());

        auditLogService.log(AuditAction.UPDATE, book);
        return book;
    }

    @Override
    public List<Book> findAll() {
        return bookRepo.findAll();
    }

    @Override 
    public Book findByIsbn(String isbn) {
        log.trace("Fetching Book: {}", isbn);
        Book book = bookRepo.findByIsbn(isbn).orElseThrow(() 
            -> new ResourceNotFoundException("Book not found"));
        return book;
    }

    @Override 
    public Page<Book> findBySearchCriteria(Specification<Book> spec, Pageable page) {
        Page<Book> searchResult = bookRepo.findAll(spec, page);
        return searchResult;
    }


    @Override
    public boolean existByIsbn(String isbn) {
        return bookRepo.existsByIsbn(isbn);
    }
}
