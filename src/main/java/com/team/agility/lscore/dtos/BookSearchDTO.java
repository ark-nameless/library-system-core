package com.team.agility.lscore.dtos;

import java.util.List;

import com.team.agility.lscore.models.SearchCriteria;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchDTO {
    private List<SearchCriteria> searchCriteriaList;
    private String dataOption;
}
