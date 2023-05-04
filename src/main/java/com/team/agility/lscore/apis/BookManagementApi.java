package com.team.agility.lscore.apis;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.BookSearchDTO;
import com.team.agility.lscore.dtos.CreateNewBookDTO;
import com.team.agility.lscore.dtos.UpdateBookDTO;
import com.team.agility.lscore.entities.Book;
import com.team.agility.lscore.models.SearchCriteria;
import com.team.agility.lscore.services.BookService;
import com.team.agility.lscore.specification.BookSpecificationBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins={"*"})
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoints.BOOKS_V1)
@SecurityRequirement(name = Constants.SECURITY_REQUIREMENT)
@Tag(name = "Book Management API", description = "Endpoint for managing book catalog")
public class BookManagementApi {

    private final BookService bookService;
    

    @Operation(summary = "Get All Books",
        description = "This endpoint is used to fetch all books in the catalog"
    )
    @GetMapping("")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok().body(bookService.findAll());
    }

    @Operation(summary = "Get Book",
        description = "This endpoint is used to fetch a book in the catalog using book's isbn"
    )
    @GetMapping("/{isbn}")
    public ResponseEntity<Book> getBook(
        @PathVariable @Valid String isbn  
    ) {
        isbn = isbn.replaceAll("[^\\d.]", "");
        return ResponseEntity.ok().body(bookService.findByIsbn(isbn));
    }

    @Operation(summary = "Advanced Search Books",
        description = "This endpoint is used to fetch books using JPA Specifiation advanced search")
    @PostMapping("/search")
    public ResponseEntity<?> advanceSearchBooks(
        @RequestParam(name = "pageNum", defaultValue = "0") int pageNum,
        @RequestParam(name = "pageSize", defaultValue = "10") int pageSize, 
        @RequestBody BookSearchDTO bookSearch
    ) {
        BookSpecificationBuilder builder = new BookSpecificationBuilder();
        List<SearchCriteria> criteriaList = bookSearch.getSearchCriteriaList();
        if (criteriaList != null) {
            criteriaList.forEach(x -> {
                x.setDataOption(bookSearch.getDataOption());
                builder.with(x);
            });
        }

        Pageable page = PageRequest.of(pageNum, pageSize, 
            Sort.by("title")
                .ascending()
                .and(Sort.by("publicationDate"))
                .ascending());
        
        Page<Book> bookPage = bookService.findBySearchCriteria(builder.build(), page);

        return ResponseEntity.ok().body(bookPage.toList());
    }
    

    @Operation(summary = "Add new Book",
        description = "This endpoint is used to add new book in the catalog"
    )
    @PostMapping("")
    public ResponseEntity<?> addNewBook(
        @RequestBody CreateNewBookDTO newBook
    ) {
        Date datePublished = new Date();
        try {
            datePublished = new SimpleDateFormat("yyyy-MM-dd").parse(newBook.getPublicationDate());
        } catch (Exception e) {
            datePublished = null;
        }
        Book book = Book.builder()
            .isbn(newBook.getIsbn())
            .author(newBook.getAuthor())
            .title(newBook.getTitle())
            .description(newBook.getDescription())
            .publicationDate(datePublished)
            .build();

        return ResponseEntity.ok().body(bookService.save(book));
    }


    @Operation(summary = "Add Books Using Excel",
        description = "This endpoint is used to add bulk of books using excel file")
    @RequestMapping(
            path = "/bulk", 
            method = RequestMethod.POST, 
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<Book>> registerUsersUsingExcel(
        @RequestParam("file") MultipartFile file
    ) throws IOException, ParseException {
        List<Book> errorAddBook = new ArrayList<>();
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);
        
        errorAddBook = bookService.saveWithExcel(sheet);

        workbook.close();
        return ResponseEntity.ok().body(errorAddBook);
    }



    @Operation(summary = "Update book",
        description = "This endpoint is used to update book in the catalog using isbn"
    )
    @PutMapping("/{isbn}")
    public ResponseEntity<Book> updateBook(
        @PathVariable @Valid String isbn,
        @RequestBody UpdateBookDTO updateBook
    ) {
        Date datePublished = new Date();
        try {
            datePublished = new SimpleDateFormat("yyyy-MM-dd").parse(updateBook.getPublicationDate());
        } catch (Exception e) {
            datePublished = null;
        }
        isbn = isbn.replaceAll("[^\\d.]", "");

        Book update = Book.builder()
            .isbn(isbn)
            .author(updateBook.getAuthor())
            .title(updateBook.getTitle())
            .description(updateBook.getDescription())
            .publicationDate(datePublished)
            .build();

        return ResponseEntity.ok().body(bookService.update(update));
    }

    @Operation(summary = "Remove book",
        description = "This endpoint is used to remove book in the catalog using isbn"
    )
    @DeleteMapping("/{isbn}")
    public ResponseEntity<?> removeBook(
        @PathVariable @Valid String isbn
    ) {
        isbn = isbn.replaceAll("[^\\d.]", "");
        bookService.remove(isbn);
        return ResponseEntity.ok().build();
    }
}
