package ru.ssau.tk.NAME.PROJECT.search;

import ru.ssau.tk.NAME.PROJECT.entity.User;
import ru.ssau.tk.NAME.PROJECT.exceptions.SearchException;
import ru.ssau.tk.NAME.PROJECT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserSearchStrategy implements SearchStrategy<User> {

    private final UserRepository userRepository;

    @Override
    public List<User> search(SearchRequest request) {
        logSearchStart(request);

        List<User> results = new ArrayList<>();

        try {
            if (request.getSearchType() == SearchRequest.SearchType.SINGLE) {
                results = executeSingleSearch(request);
            } else {
                results = executeMultipleSearch(request);
            }

            results = applySorting(results, request);
            results = applyPagination(results, request);

        } catch (Exception e) {
            log.error("Ошибка при поиске пользователей: {}", e.getMessage(), e);
            throw new SearchException("Поиск не удался: " + e.getMessage(), e);
        }

        logSearchResults(results);
        return results;
    }

    private void logSearchStart(SearchRequest request) {
        log.info("Начало поиска {} с критериями: {}", getStrategyName(), request.getCriteria());
    }

    private void logSearchResults(List<User> results) {
        log.info("Поиск {} завершен. Найдено результатов: {}", getStrategyName(), results.size());
        if (log.isDebugEnabled()) {
            log.debug("🔍 Детали найденных результатов:");
            results.forEach(result -> log.debug("   Найден: {}", result));
        }
    }

    private List<User> executeSingleSearch(SearchRequest request) {
        if (request.getCriteria().isEmpty()) {
            log.debug("Критерии поиска не указаны, возвращаем всех пользователей");
            return userRepository.findAll();
        }

        SearchCriteria criteria = request.getCriteria().get(0);
        return switch (criteria.getOperation()) {
            case EQUALS -> searchByEquals(criteria);
            case LIKE -> searchByLike(criteria);
            case IN -> searchByIn(criteria);
            default -> throw new UnsupportedOperationException("Операция не поддерживается: " + criteria.getOperation());
        };
    }

    private List<User> executeMultipleSearch(SearchRequest request) {
        List<User> results = new ArrayList<>();

        for (SearchCriteria criteria : request.getCriteria()) {
            List<User> partialResults = switch (criteria.getOperation()) {
                case EQUALS -> searchByEquals(criteria);
                case LIKE -> searchByLike(criteria);
                case IN -> searchByIn(criteria);
                default -> throw new UnsupportedOperationException("Операция не поддерживается: " + criteria.getOperation());
            };

            if (results.isEmpty()) {
                results.addAll(partialResults);
            } else {
                results.retainAll(partialResults);
            }

            log.debug("После применения критерия {}: найдено {} пользователей", criteria.getFieldName(), results.size());
        }

        return results;
    }

    private List<User> searchByEquals(SearchCriteria criteria) {
        return switch (criteria.getFieldName()) {
            case "name" -> userRepository.findByName((String) criteria.getValue())
                    .map(List::of).orElse(List.of());
            case "role" -> {
                if (criteria.getValue() instanceof User.Role) {
                    yield userRepository.findByRole((User.Role) criteria.getValue());
                } else {
                    throw new IllegalArgumentException("Значение для role должно быть типа User.Role");
                }
            }
            default -> throw new IllegalArgumentException("Неподдерживаемое поле: " + criteria.getFieldName());
        };
    }

    private List<User> searchByLike(SearchCriteria criteria) {
        if ("name".equals(criteria.getFieldName())) {
            return userRepository.findByNameContaining((String) criteria.getValue());
        }
        throw new IllegalArgumentException("Операция LIKE поддерживается только для поля name");
    }

    private List<User> searchByIn(SearchCriteria criteria) {
        if ("role".equals(criteria.getFieldName())) {
            if (criteria.getValues() != null) {
                // Безопасное преобразование с проверкой типов
                List<User.Role> roles = criteria.getValues().stream()
                        .filter(value -> value instanceof User.Role)
                        .map(value -> (User.Role) value)
                        .collect(Collectors.toList());

                if (roles.isEmpty()) {
                    throw new IllegalArgumentException("Список ролей должен содержать значения типа User.Role");
                }

                return userRepository.findByRoles(roles);
            } else {
                throw new IllegalArgumentException("Для операции IN должен быть указан список значений");
            }
        }
        throw new IllegalArgumentException("Операция IN поддерживается только для поля role");
    }

    private List<User> applySorting(List<User> users, SearchRequest request) {
        if (request.getSortBy() != null) {
            users.sort((u1, u2) -> {
                int result = switch (request.getSortBy()) {
                    case "name" -> u1.getName().compareTo(u2.getName());
                    case "role" -> u1.getRole().compareTo(u2.getRole());
                    default -> 0;
                };
                return request.isAscending() ? result : -result;
            });
            log.debug("Применена сортировка по {} {}", request.getSortBy(),
                    request.isAscending() ? "по возрастанию" : "по убыванию");
        }
        return users;
    }

    private List<User> applyPagination(List<User> users, SearchRequest request) {
        int start = request.getPage() * request.getSize();
        int end = Math.min(start + request.getSize(), users.size());

        if (start >= users.size()) {
            return List.of();
        }

        List<User> paginated = users.subList(start, end);
        log.debug("Применена пагинация: страница {}, размер {}, результаты {}-{}",
                request.getPage(), request.getSize(), start, end);

        return paginated;
    }

    @Override
    public String getStrategyName() {
        return "Поиск пользователей";
    }
}
