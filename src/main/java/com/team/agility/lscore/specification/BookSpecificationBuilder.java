package com.team.agility.lscore.specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.team.agility.lscore.entities.Book;
import com.team.agility.lscore.enums.SearchOperation;
import com.team.agility.lscore.models.SearchCriteria;

public class BookSpecificationBuilder {
    private final List<SearchCriteria> params;

    public BookSpecificationBuilder() {
        this.params = new ArrayList<>();
    }

    public final BookSpecificationBuilder with(String key, String operation, Object value){
        params.add(new SearchCriteria(key, operation, value));
        return this;
    }

    public final BookSpecificationBuilder with(SearchCriteria searchCriteria){
        params.add(searchCriteria);
        return this;
    }

    public Specification<Book> build(){
        if(params.size() == 0){
            return null;
        }

        Specification<Book> result = new BookSpecification(params.get(0));
        for (int idx = 1; idx < params.size(); idx++){
            SearchCriteria criteria = params.get(idx);
            result =  SearchOperation.getDataOption(criteria.getDataOption()) == SearchOperation.ALL
                     ? Specification.where(result).and(new BookSpecification(criteria))
                     : Specification.where(result).or(new BookSpecification(criteria));
        }
        return result;
    }
}
