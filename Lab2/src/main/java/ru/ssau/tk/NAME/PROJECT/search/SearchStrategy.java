package ru.ssau.tk.NAME.PROJECT.search;

import java.util.List;

public interface SearchStrategy<T> {
    List<T> search(SearchRequest request);
    String getStrategyName();
}
