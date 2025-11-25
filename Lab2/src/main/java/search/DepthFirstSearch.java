// search/DepthFirstSearch.java
package search;

import modelDB.User;
import modelDB.Function;
import modelDB.Point;
import service.DTOTransformService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class DepthFirstSearch implements SearchAlgorithm<Object> {
    private final DTOTransformService dtoService;

    public DepthFirstSearch(DTOTransformService dtoService) {
        this.dtoService = dtoService;
    }

    @Override
    public List<Object> search(List<Object> data, String field, String value) {
        log.debug("Поиск в глубину по полю '{}' со значением '{}'", field, value);

        List<Object> results = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();
        long startTime = System.currentTimeMillis();

        for (Object item : data) {
            if (item instanceof User) {
                dfsUser((User) item, field, value, results, visited);
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Поиск в глубину завершен: найдено {} объектов за {} мс",
                results.size(), duration);

        return results;
    }

    private void dfsUser(User user, String field, String value,
                         List<Object> results, Set<UUID> visited) {
        if (visited.contains(user.getId())) {
            return;
        }

        visited.add(user.getId());

        // Проверяем пользователя
        if (FieldHelper.matchesField(user, field, value)) {
            results.add(user);
        }

        // Рекурсивно обходим функции пользователя
        List<Function> functions = dtoService.getFunctionsByOwner(user.getId());
        for (Function function : functions) {
            dfsFunction(function, field, value, results, visited);
        }
    }

    private void dfsFunction(Function function, String field, String value,
                             List<Object> results, Set<UUID> visited) {
        if (visited.contains(function.getId())) {
            return;
        }

        visited.add(function.getId());

        // Проверяем функцию
        if (FieldHelper.matchesField(function, field, value)) {
            results.add(function);
        }

        // Рекурсивно обходим точки функции
        List<Point> points = dtoService.getPointsByFunction(function.getId());
        for (Point point : points) {
            if (!visited.contains(point.getId())) {
                visited.add(point.getId());
                if (FieldHelper.matchesField(point, field, value)) {
                    results.add(point);
                }
            }
        }
    }

    @Override
    public String getAlgorithmName() {
        return "Depth First Search";
    }
}