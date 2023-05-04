package com.team.agility.lscore.dtos;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BorrowDetailsDTO {
    @NotEmpty
    @Schema(description = "Book's ISBN(International Standard Book Number)", example = "9783319516554") 
    private String isbn;

    @NotEmpty
    @Schema(description = "Book's Publication Date(YYYY-MM-DD)", example = "2017-02-01")
    private String returnDate;
}
