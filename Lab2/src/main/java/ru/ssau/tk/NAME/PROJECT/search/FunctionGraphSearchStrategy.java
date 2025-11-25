package ru.ssau.tk.NAME.PROJECT.search;

import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import ru.ssau.tk.NAME.PROJECT.repository.FunctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FunctionGraphSearchStrategy implements GraphSearchStrategy<Function> {

    private final FunctionRepository functionRepository;

    @Override
    public List<Function> search(SearchRequest request) {
        logSearchStart(request);
        List<Function> results = hierarchicalSearch(null, request);
        logSearchResults(results);
        return results;
    }

    @Override
    public List<Function> depthFirstSearch(Function startNode, SearchRequest request) {
        log.info("Начало поиска в глубину для функций от узла: {}",
                startNode != null ? startNode.getId() : "корень");
        List<Function> results = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();
        Predicate<Function> predicate = createPredicate(request);
        if (startNode != null) {
            dfs(startNode, visited, results, predicate);
        } else {
            List<Function> allFunctions = functionRepository.findAll();
            for (Function function : allFunctions) {
                if (!visited.contains(function.getId())) {
                    dfs(function, visited, results, predicate);
                }
            }
        }
        log.debug("Поиск в глубину завершен. Посещено узлов: {}, найдено результатов: {}",
                visited.size(), results.size());
        return applySortingAndPagination(results, request);
    }

    @Override
    public List<Function> breadthFirstSearch(Function startNode, SearchRequest request) {
        log.info("Начало поиска в ширину для функций от узла: {}",
                startNode != null ? startNode.getId() : "корень");
        List<Function> results = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();
        Queue<Function> queue = new LinkedList<>();
        Predicate<Function> predicate = createPredicate(request);
        if (startNode != null) {
            queue.offer(startNode);
            visited.add(startNode.getId());
        } else {
            functionRepository.findAll().forEach(func -> {
                queue.offer(func);
                visited.add(func.getId());
            });
        }
        while (!queue.isEmpty()) {
            Function current = queue.poll();
            if (predicate.test(current)) {
                results.add(current);
            }

            List<Function> neighbors = findNeighbors(current);
            for (Function neighbor : neighbors) {
                if (!visited.contains(neighbor.getId())) {
                    visited.add(neighbor.getId());
                    queue.offer(neighbor);
                }
            }
        }
        log.debug("Поиск в ширину завершен. Посещено узлов: {}, найдено результатов: {}",
                visited.size(), results.size());
        return applySortingAndPagination(results, request);
    }

    @Override
    public List<Function> hierarchicalSearch(Function rootNode, SearchRequest request) {
        log.info("Начало иерархического поиска для функций");
        List<Function> results = new ArrayList<>();
        Predicate<Function> predicate = createPredicate(request);
        if (rootNode != null) {
            User owner = rootNode.getOwner();
            results = functionRepository.findByOwner(owner).stream()
                    .filter(predicate)
                    .collect(Collectors.toList());
        } else {
            results = functionRepository.findAll().stream()
                    .filter(predicate)
                    .collect(Collectors.toList());
        }
        log.debug("Иерархический поиск завершен. Найдено результатов: {}", results.size());
        return applySortingAndPagination(results, request);
    }

    private void logSearchStart(SearchRequest request) {
        log.info("Начало поиска {} с критериями: {}", getStrategyName(), request.getCriteria());
    }

    private void logSearchResults(List<Function> results) {
        log.info("Поиск {} завершен. Найдено результатов: {}", getStrategyName(), results.size());
        if (log.isDebugEnabled()) {
            log.debug("Детали найденных результатов:");
            results.forEach(result -> log.debug("   Найден: {}", result));
        }
    }

    private void dfs(Function node, Set<UUID> visited, List<Function> results, Predicate<Function> predicate) {
        visited.add(node.getId());
        if (predicate.test(node)) {
            results.add(node);
        }
        List<Function> neighbors = findNeighbors(node);
        for (Function neighbor : neighbors) {
            if (!visited.contains(neighbor.getId())) {
                dfs(neighbor, visited, results, predicate);
            }
        }
    }

    private List<Function> findNeighbors(Function function) {
        List<Function> neighbors = new ArrayList<>();
        neighbors.addAll(functionRepository.findByOwner(function.getOwner()));
        List<Function> sameType = functionRepository.findByType(function.getType()).stream()
                .filter(f -> !f.getId().equals(function.getId()))
                .collect(Collectors.toList());
        neighbors.addAll(sameType);
        return neighbors.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    private Predicate<Function> createPredicate(SearchRequest request) {
        return function -> {
            if (request.getCriteria().isEmpty()) {
                return true;
            }
            for (SearchCriteria criteria : request.getCriteria()) {
                boolean matches = switch (criteria.getFieldName()) {
                    case "name" -> matchesName(function, criteria);
                    case "type" -> matchesType(function, criteria);
                    case "owner" -> matchesOwner(function, criteria);
                    default -> true;
                };

                if (!matches) {
                    return false;
                }
            }
            return true;
        };
    }

    private boolean matchesName(Function function, SearchCriteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALS -> function.getName().equals(criteria.getValue());
            case LIKE -> function.getName().contains((String) criteria.getValue());
            default -> false;
        };
    }

    private boolean matchesType(Function function, SearchCriteria criteria) {
        return switch (criteria.getOperation()) {
            case EQUALS -> function.getType().equals(criteria.getValue());
            case LIKE -> function.getType().contains((String) criteria.getValue());
            default -> false;
        };
    }

    private boolean matchesOwner(Function function, SearchCriteria criteria) {
        if (criteria.getOperation() == SearchOperation.EQUALS) {
            User owner = (User) criteria.getValue();
            return function.getOwner().getId().equals(owner.getId());
        }
        return false;
    }

    private List<Function> applySortingAndPagination(List<Function> functions, SearchRequest request) {
        if (request.getSortBy() != null) {
            functions.sort((f1, f2) -> {
                int result = switch (request.getSortBy()) {
                    case "name" -> f1.getName().compareTo(f2.getName());
                    case "type" -> f1.getType().compareTo(f2.getType());
                    default -> 0;
                };
                return request.isAscending() ? result : -result;
            });
            log.debug("Применена сортировка функций по {}", request.getSortBy());
        }
        int start = request.getPage() * request.getSize();
        int end = Math.min(start + request.getSize(), functions.size());
        if (start >= functions.size()) {
            return List.of();
        }

        log.debug("Применена пагинация функций: страница {}", request.getPage());
        return functions.subList(start, end);
    }

    @Override
    public String getStrategyName() {
        return "Поиск функций";
    }
}
