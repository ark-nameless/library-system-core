package com.team.agility.lscore.entities;


import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table
@JsonInclude
public class TokenExpiration {
    @Id
    @Column(unique = true)
    private String name;

    @PositiveOrZero
    private int days;

    @PositiveOrZero
    private int hours;

    @PositiveOrZero
    private int minutes;
}
