// search/SearchAlgorithm.java
package search;

import java.util.List;

public interface SearchAlgorithm<T> {
    List<T> search(List<T> data, String field, String value);
    String getAlgorithmName();
}