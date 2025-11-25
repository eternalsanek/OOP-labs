package ru.ssau.tk.NAME.PROJECT.search;

import java.util.List;

public interface GraphSearchStrategy<T> extends SearchStrategy<T> {
    List<T> depthFirstSearch(T startNode, SearchRequest request);
    List<T> breadthFirstSearch(T startNode, SearchRequest request);
    List<T> hierarchicalSearch(T rootNode, SearchRequest request);
}
