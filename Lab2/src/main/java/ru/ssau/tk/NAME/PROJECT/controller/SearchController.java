package ru.ssau.tk.NAME.PROJECT.controller;

import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import ru.ssau.tk.NAME.PROJECT.repository.FunctionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.NAME.PROJECT.search.SearchRequest;
import ru.ssau.tk.NAME.PROJECT.search.SearchService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final FunctionRepository functionRepository;

    @PostMapping("/users")
    public ResponseEntity<List<User>> searchUsers(@RequestBody SearchRequest request) {
        log.info("Поиск пользователей: {} критериев", request.getCriteria().size());
        try {
            List<User> results = searchService.searchUsers(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Ошибка поиска пользователей: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/functions")
    public ResponseEntity<List<Function>> searchFunctions(@RequestBody SearchRequest request) {
        log.info("Поиск функций: {} критериев", request.getCriteria().size());
        try {
            List<Function> results = searchService.searchFunctions(request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Ошибка поиска функций: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/functions/{functionId}/points")
    public ResponseEntity<List<Point>> searchPointsByFunction(
            @PathVariable UUID functionId,
            @RequestBody SearchRequest request) {
        log.info("Поиск точек для функции {}: {} критериев", functionId, request.getCriteria().size());
        try {
            Optional<Function> function = functionRepository.findById(functionId);
            if (function.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<Point> results = searchService.searchPointsByFunction(function.get(), request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Ошибка поиска точек: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/functions/dfs/{functionId}")
    public ResponseEntity<List<Function>> depthFirstSearch(
            @PathVariable UUID functionId,
            @RequestBody SearchRequest request) {
        log.info("Поиск в глубину для функции: {}", functionId);
        try {
            Optional<Function> function = functionRepository.findById(functionId);
            if (function.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<Function> results = searchService.depthFirstSearch(function.get(), request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Ошибка поиска в глубину: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/functions/bfs/{functionId}")
    public ResponseEntity<List<Function>> breadthFirstSearch(
            @PathVariable UUID functionId,
            @RequestBody SearchRequest request) {
        log.info("Поиск в ширину для функции: {}", functionId);
        try {
            Optional<Function> function = functionRepository.findById(functionId);
            if (function.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<Function> results = searchService.breadthFirstSearch(function.get(), request);
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            log.error("Ошибка поиска в ширину: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
