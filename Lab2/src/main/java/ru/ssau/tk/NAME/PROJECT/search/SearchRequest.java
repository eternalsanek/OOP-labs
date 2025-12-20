package ru.ssau.tk.NAME.PROJECT.search;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SearchRequest {
    private List<SearchCriteria> criteria = new ArrayList<>();
    private String sortBy;
    private boolean ascending = true;
    private int page = 0;
    private int size = 50;
    private SearchType searchType = SearchType.SINGLE;

    public enum SearchType {
        SINGLE,
        MULTIPLE
    }
}
