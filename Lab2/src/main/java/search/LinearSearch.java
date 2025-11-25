// search/LinearSearch.java
package search;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class LinearSearch<T> implements SearchAlgorithm<T> {

    @Override
    public List<T> search(List<T> data, String field, String value) {
        log.debug("Линейный поиск по полю '{}' со значением '{}' в {} элементах",
                field, value, data.size());

        long startTime = System.currentTimeMillis();

        List<T> results = data.stream()
                .filter(item -> FieldHelper.matchesField(item, field, value))
                .collect(Collectors.toList());

        long duration = System.currentTimeMillis() - startTime;
        log.info("Линейный поиск завершен: найдено {} результатов за {} мс",
                results.size(), duration);

        return results;
    }

    @Override
    public String getAlgorithmName() {
        return "Linear Search";
    }
}