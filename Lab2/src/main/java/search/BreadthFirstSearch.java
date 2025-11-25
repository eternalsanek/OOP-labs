// search/BreadthFirstSearch.java
package search;

import modelDB.User;
import modelDB.Function;
import modelDB.Point;
import service.DTOTransformService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class BreadthFirstSearch implements SearchAlgorithm<Object> {
    private final DTOTransformService dtoService;

    public BreadthFirstSearch(DTOTransformService dtoService) {
        this.dtoService = dtoService;
    }

    @Override
    public List<Object> search(List<Object> data, String field, String value) {
        log.debug("Поиск в ширину по полю '{}' со значением '{}'", field, value);

        List<Object> results = new ArrayList<>();
        Set<UUID> visited = new HashSet<>();
        Queue<Object> queue = new LinkedList<>();
        long startTime = System.currentTimeMillis();

        // Начинаем с пользователей
        for (Object item : data) {
            if (item instanceof User) {
                queue.offer(item);
            }
        }

        while (!queue.isEmpty()) {
            Object current = queue.poll();

            if (visited.contains(getId(current))) {
                continue;
            }

            visited.add(getId(current));

            // Проверяем текущий объект
            if (FieldHelper.matchesField(current, field, value)) {
                results.add(current);
            }

            // Добавляем дочерние объекты в очередь
            addChildrenToQueue(current, queue, visited);
        }

        long duration = System.currentTimeMillis() - startTime;
        log.info("Поиск в ширину завершен: найдено {} объектов за {} мс",
                results.size(), duration);

        return results;
    }

    private void addChildrenToQueue(Object current, Queue<Object> queue, Set<UUID> visited) {
        if (current instanceof User) {
            User user = (User) current;
            List<Function> functions = dtoService.getFunctionsByOwner(user.getId());
            for (Function function : functions) {
                if (!visited.contains(function.getId())) {
                    queue.offer(function);
                }
            }
        } else if (current instanceof Function) {
            Function function = (Function) current;
            List<Point> points = dtoService.getPointsByFunction(function.getId());
            for (Point point : points) {
                if (!visited.contains(point.getId())) {
                    queue.offer(point);
                }
            }
        }
    }

    private UUID getId(Object obj) {
        if (obj instanceof User) {
            return ((User) obj).getId();
        } else if (obj instanceof Function) {
            return ((Function) obj).getId();
        } else if (obj instanceof Point) {
            return ((Point) obj).getId();
        }
        return UUID.randomUUID(); // fallback
    }

    @Override
    public String getAlgorithmName() {
        return "Breadth First Search";
    }
}