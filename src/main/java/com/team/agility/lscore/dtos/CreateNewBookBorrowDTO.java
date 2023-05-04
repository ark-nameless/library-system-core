package com.team.agility.lscore.dtos;

import java.util.Date;
import java.util.Set;
import java.util.List;

import com.team.agility.lscore.models.BookBorrowDetails;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNewBookBorrowDTO {

    @NotEmpty
    @Schema(description = "Issuer's Username", example = "admin")
    private String issuer;

    @NotEmpty
    @Schema(description = "Borrower's Username", example = "ark_zero")
    private String borrower;

    @NotEmpty
    @Schema(description = "List of Books that will be Borrowed"
    )
    private Set<BorrowDetailsDTO> listOfBooks;
}