package com.team.agility.lscore.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team.agility.lscore.models.BookBorrowDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "borrowed_books")
public class BorrowBook {
    @Id
    @GeneratedValue
    private Long id; 

    @Builder.Default
    @Column(columnDefinition = "boolean default false")
    private Boolean isReturned = false;

    private Long issuerId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User borrower;

    @Column(nullable = false)
    @Embedded
    private Set<BookBorrowDetails> borrowedBook;

    @Column(name="issue_date", nullable = false, updatable = false)
    @CreationTimestamp
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date issueDate;
}
