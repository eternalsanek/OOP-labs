// search/SearchService.java
package search;

import modelDB.User;
import modelDB.Function;
import modelDB.Point;
import service.DTOTransformService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SearchService {
    private final DTOTransformService dtoService;
    private final Map<String, SearchAlgorithm<?>> algorithms;

    public SearchService(DTOTransformService dtoService) {
        this.dtoService = dtoService;
        this.algorithms = new HashMap<>();
        initializeAlgorithms();
        log.debug("Инициализирован SearchService");
    }

    private void initializeAlgorithms() {
        // Алгоритмы для плоских коллекций
        algorithms.put("linear", new LinearSearch<>());
        algorithms.put("binary", new BinarySearch<>());

        // Алгоритмы для иерархического поиска
        algorithms.put("dfs", new DepthFirstSearch(dtoService));
        algorithms.put("bfs", new BreadthFirstSearch(dtoService));
    }

    // Одиночный поиск
    public <T> Optional<T> findOne(Class<T> type, String field, String value, String algorithm) {
        log.debug("Одиночный поиск: тип={}, поле={}, значение={}, алгоритм={}",
                type.getSimpleName(), field, value, algorithm);

        List<T> results = findAll(type, field, value, algorithm, 1);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    // Множественный поиск
    public <T> List<T> findAll(Class<T> type, String field, String value, String algorithm) {
        return findAll(type, field, value, algorithm, Integer.MAX_VALUE);
    }

    // Множественный поиск с лимитом
    public <T> List<T> findAll(Class<T> type, String field, String value,
                               String algorithm, int limit) {
        log.debug("Множественный поиск: тип={}, поле={}, значение={}, алгоритм={}, лимит={}",
                type.getSimpleName(), field, value, algorithm, limit);

        List<T> data = getDataByType(type);
        List<T> results = performSearch(type, data, field, value, algorithm);

        if (results.size() > limit) {
            results = results.subList(0, limit);
        }

        log.info("Найдено {} результатов типа {}", results.size(), type.getSimpleName());
        return results;
    }

    // Поиск с сортировкой
    public <T> List<T> findAllSorted(Class<T> type, String field, String value,
                                     String algorithm, String sortField, boolean ascending) {
        log.debug("Поиск с сортировкой: тип={}, поле={}, сортировка по={}, порядок={}",
                type.getSimpleName(), field, sortField, ascending ? "ASC" : "DESC");

        List<T> results = findAll(type, field, value, algorithm);
        return sortResults(results, sortField, ascending);
    }

    // Приватные вспомогательные методы
    private <T> List<T> getDataByType(Class<T> type) {
        if (type.equals(User.class)) {
            return (List<T>) dtoService.getAllUsers();
        } else if (type.equals(Function.class)) {
            List<Function> allFunctions = new ArrayList<>();
            List<User> users = dtoService.getAllUsers();
            for (User user : users) {
                allFunctions.addAll(dtoService.getFunctionsByOwner(user.getId()));
            }
            return (List<T>) allFunctions;
        } else if (type.equals(Point.class)) {
            List<Point> allPoints = new ArrayList<>();
            List<User> users = dtoService.getAllUsers();
            for (User user : users) {
                List<Function> functions = dtoService.getFunctionsByOwner(user.getId());
                for (Function function : functions) {
                    allPoints.addAll(dtoService.getPointsByFunction(function.getId()));
                }
            }
            return (List<T>) allPoints;
        } else if (type.equals(Object.class)) {
            // Для иерархического поиска возвращаем пользователей как корневые элементы
            return (List<T>) dtoService.getAllUsers();
        }
        throw new IllegalArgumentException("Неподдерживаемый тип: " + type.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> performSearch(Class<T> type, List<T> data, String field,
                                      String value, String algorithm) {
        SearchAlgorithm<T> searchAlgorithm = (SearchAlgorithm<T>) algorithms.get(algorithm.toLowerCase());
        if (searchAlgorithm == null) {
            log.warn("Алгоритм '{}' не найден, используется линейный поиск", algorithm);
            searchAlgorithm = (SearchAlgorithm<T>) algorithms.get("linear");
        }

        return searchAlgorithm.search(data, field, value);
    }

    private <T> List<T> sortResults(List<T> results, String sortField, boolean ascending) {
        results.sort((o1, o2) -> {
            Comparable<?> val1 = FieldHelper.getComparableFieldValue(o1, sortField);
            Comparable<?> val2 = FieldHelper.getComparableFieldValue(o2, sortField);

            int comparison;
            if (val1 == null && val2 == null) {
                comparison = 0;
            } else if (val1 == null) {
                comparison = -1;
            } else if (val2 == null) {
                comparison = 1;
            } else {
                // Безопасное сравнение через строковое представление
                comparison = val1.toString().compareTo(val2.toString());
            }

            return ascending ? comparison : -comparison;
        });

        log.debug("Результаты отсортированы по полю '{}'", sortField);
        return results;
    }

    // Метод для получения информации об алгоритмах
    public List<String> getAvailableAlgorithms() {
        return algorithms.values().stream()
                .map(SearchAlgorithm::getAlgorithmName)
                .distinct()
                .collect(Collectors.toList());
    }
}