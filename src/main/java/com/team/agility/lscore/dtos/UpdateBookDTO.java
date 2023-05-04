package com.team.agility.lscore.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookDTO {
    @Schema(description = "Book's Title", example = "Pervasive Computing: Engineering Smart Systems") 
    private String title;

    @Schema(description = "Book's Author", example = "Natalia Silvis-Cividjian") 
    private String author;

    @Schema(description = "Book's Description", example = "") 
    private String description;

    @Schema(description = "Book's Publication Date(YYYY-MM-DD)", example = "2017-02-01")
    private String publicationDate;
}
