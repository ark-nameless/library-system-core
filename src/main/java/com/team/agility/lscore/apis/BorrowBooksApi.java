package com.team.agility.lscore.apis;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team.agility.lscore.constants.Constants;
import com.team.agility.lscore.constants.Endpoints;
import com.team.agility.lscore.dtos.CreateNewBookBorrowDTO;
import com.team.agility.lscore.entities.BorrowBook;
import com.team.agility.lscore.services.BookBorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins={"*"})
@RestController
@RequiredArgsConstructor
@RequestMapping(Endpoints.BORROW_BOOKS_V1)
@SecurityRequirement(name = Constants.SECURITY_REQUIREMENT)
@Tag(name = "Borrow Books API", description = "Borrow books management API")
public class BorrowBooksApi {
    private final BookBorrowService borrowService;
    

    @Operation(summary = "Fetch all Books Borrowed",
        description = "This endpoint is used to fetch all records of books borrowed."    
    )
    @GetMapping("")
    public ResponseEntity<List<BorrowBook>> getAllBooksBorrowed() {
        return ResponseEntity.ok().body(borrowService.findAll());
    }

    @Operation(summary = "Fetch all Books Borrowed by a User",
        description = "This endpoint is used to fetch all records of books borrowed by specific user."    
    )
    @GetMapping("/{username}/find")
    public ResponseEntity<?> findUserBorrowedBooks(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username
    ) {
        return ResponseEntity.ok().body(borrowService.findByBorrowerUsername(username));
    }

    @Operation(summary = "Check if Users borrow book status",
        description = "This endpoint is  used to check if a user has an outstanding book borrow record.")
    @GetMapping("/{username}/borrowed")
    public ResponseEntity<Boolean> checkIfUserHasBorrowedBook(
        @PathVariable @NotBlank @Size(min = 3, max = 255) String username
    ) {
        return ResponseEntity.ok().body(borrowService.hasOutstandingBorrow(username));
    }


    @Operation(summary = "Create new Borrow Request",
        description = "This endpiont is used to create new borrow books record"
    )
    @PostMapping("")
    public ResponseEntity<BorrowBook> borrowNewBooks(
        @RequestBody CreateNewBookBorrowDTO newBorrow
    ) {
        return ResponseEntity.ok().body(borrowService.save(newBorrow));
    }
    
    
}
