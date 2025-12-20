package ru.ssau.tk.NAME.PROJECT.search;

import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final UserSearchStrategy userSearchStrategy;
    private final FunctionGraphSearchStrategy functionSearchStrategy;

    public List<User> searchUsers(SearchRequest request) {
        log.info("👥 Выполнение поиска пользователей с {} критериями", request.getCriteria().size());
        return userSearchStrategy.search(request);
    }

    public List<Function> searchFunctions(SearchRequest request) {
        log.info("📊 Выполнение поиска функций с {} критериями", request.getCriteria().size());
        return functionSearchStrategy.search(request);
    }

    public List<Point> searchPointsByFunction(Function function, SearchRequest request) {
        log.info("📈 Поиск точек для функции {} с критериями: {}", function.getId(), request.getCriteria().size());

        // Получаем все точки функции и применяем фильтрацию
        List<Point> allPoints = function.getPoints(); // Используем связь OneToMany

        return allPoints.stream()
                .filter(point -> matchesCriteria(point, request))
                .toList();
    }

    private boolean matchesCriteria(Point point, SearchRequest request) {
        if (request.getCriteria().isEmpty()) {
            return true;
        }
        for (SearchCriteria criteria : request.getCriteria()) {
            boolean matches = switch (criteria.getFieldName()) {
                case "xVal" -> matchesXVal(point, criteria);
                case "yVal" -> matchesYVal(point, criteria);
                default -> true;
            };

            if (!matches) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesXVal(Point point, SearchCriteria criteria) {
        return matchesBigDecimalValue(point.getXVal(), criteria);
    }

    private boolean matchesYVal(Point point, SearchCriteria criteria) {
        return matchesBigDecimalValue(point.getYVal(), criteria);
    }

    private boolean matchesBigDecimalValue(BigDecimal pointValue, SearchCriteria criteria) {
        if (criteria.getValue() == null && criteria.getValues() == null) {
            return false;
        }

        return switch (criteria.getOperation()) {
            case EQUALS -> {
                if (criteria.getValue() instanceof BigDecimal) {
                    yield pointValue.compareTo((BigDecimal) criteria.getValue()) == 0;
                }
                yield false;
            }
            case GREATER_THAN -> {
                if (criteria.getValue() instanceof BigDecimal) {
                    yield pointValue.compareTo((BigDecimal) criteria.getValue()) > 0;
                }
                yield false;
            }
            case LESS_THAN -> {
                if (criteria.getValue() instanceof BigDecimal) {
                    yield pointValue.compareTo((BigDecimal) criteria.getValue()) < 0;
                }
                yield false;
            }
            case BETWEEN -> {
                if (criteria.getValues() != null && criteria.getValues().size() == 2 &&
                        criteria.getValues().get(0) instanceof BigDecimal &&
                        criteria.getValues().get(1) instanceof BigDecimal) {

                    BigDecimal min = (BigDecimal) criteria.getValues().get(0);
                    BigDecimal max = (BigDecimal) criteria.getValues().get(1);
                    yield pointValue.compareTo(min) >= 0 && pointValue.compareTo(max) <= 0;
                }
                yield false;
            }
            default -> false;
        };
    }

    public List<Function> depthFirstSearch(Function startNode, SearchRequest request) {
        log.info("Выполнение поиска в глубину для функций начиная с {}",
                startNode != null ? startNode.getId() : "всех функций");
        return functionSearchStrategy.depthFirstSearch(startNode, request);
    }

    public List<Function> breadthFirstSearch(Function startNode, SearchRequest request) {
        log.info("Выполнение поиска в ширину для функций начиная с {}",
                startNode != null ? startNode.getId() : "всех функций");
        return functionSearchStrategy.breadthFirstSearch(startNode, request);
    }

    public List<Function> hierarchicalSearch(Function rootNode, SearchRequest request) {
        log.info("Выполнение иерархический поиск для функций с корнем {}",
                rootNode != null ? rootNode.getId() : "всех иерархий");
        return functionSearchStrategy.hierarchicalSearch(rootNode, request);
    }
}
