// search/BinarySearch.java
package search;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class BinarySearch<T> implements SearchAlgorithm<T> {

    @Override
    public List<T> search(List<T> data, String field, String value) {
        log.debug("Бинарный поиск по полю '{}' со значением '{}' в {} элементах",
                field, value, data.size());

        if (data.isEmpty()) {
            return new ArrayList<>();
        }

        long startTime = System.currentTimeMillis();

        // Сортируем данные для бинарного поиска
        List<T> sortedData = new ArrayList<>(data);
        sortedData.sort(FieldHelper.createComparator(field));

        List<T> results = new ArrayList<>();
        int index = findFirstMatch(sortedData, field, value.toLowerCase());

        if (index >= 0) {
            // Добавляем все совпадения
            addMatches(sortedData, field, value.toLowerCase(), results, index);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Бинарный поиск завершен: найдено {} результатов за {} мс",
                results.size(), duration);

        return results;
    }

    private int findFirstMatch(List<T> data, String field, String searchValue) {
        int low = 0;
        int high = data.size() - 1;
        int result = -1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            String midValue = FieldHelper.getFieldValue(data.get(mid), field).toLowerCase();

            if (midValue.contains(searchValue)) {
                result = mid;
                high = mid - 1; // Ищем первое вхождение
            } else if (midValue.compareTo(searchValue) < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return result;
    }

    private void addMatches(List<T> data, String field, String searchValue,
                            List<T> results, int startIndex) {
        // Добавляем совпадения слева от startIndex
        int left = startIndex;
        while (left >= 0 && FieldHelper.getFieldValue(data.get(left), field).toLowerCase()
                .contains(searchValue)) {
            results.add(data.get(left));
            left--;
        }

        // Добавляем совпадения справа от startIndex
        int right = startIndex + 1;
        while (right < data.size() && FieldHelper.getFieldValue(data.get(right), field).toLowerCase()
                .contains(searchValue)) {
            results.add(data.get(right));
            right++;
        }
    }

    @Override
    public String getAlgorithmName() {
        return "Binary Search";
    }
}