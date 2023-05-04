package com.team.agility.lscore.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class BookBorrowDetails {
    private String isbn; 
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date returnDate;
}
