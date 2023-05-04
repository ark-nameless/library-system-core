package com.team.agility.lscore.entities;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.team.agility.lscore.serializers.CustomDateSerializer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue
    private Long id; 

    @Column(unique = true)
    private String isbn;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(columnDefinition = "TEXT")
    private String description;

    @JsonFormat(pattern="yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date publicationDate;
}
