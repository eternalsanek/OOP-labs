package ru.ssau.tk.NAME.PROJECT.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.ssau.tk.NAME.PROJECT.dto.FunctionDTO;
import ru.ssau.tk.NAME.PROJECT.dto.PointDTO;
import ru.ssau.tk.NAME.PROJECT.security.FunctionOwnerOnly;
import ru.ssau.tk.NAME.PROJECT.service.FunctionService;
import ru.ssau.tk.NAME.PROJECT.security.OwnerOnly;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.ssau.tk.NAME.PROJECT.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/functions")
@RequiredArgsConstructor
public class FunctionController {

    private final FunctionService functionService;
    private final UserService userService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FunctionDTO>> getAllFunctions() {
        log.info("Getting all functions for authenticated user");
        List<FunctionDTO> functions = functionService.getAllFunctions();
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FunctionDTO> getFunctionById(@PathVariable UUID id) {
        log.info("Getting function with id: {}", id);
        return functionService.getFunctionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FunctionDTO>> getMyFunctions() {
        log.info("Getting functions for the current user");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // Получаем имя текущего пользователя
        List<FunctionDTO> functions = functionService.getFunctionsByOwner(username);
        return ResponseEntity.ok(functions);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    public ResponseEntity<?> createFunction(@RequestBody FunctionDTO functionDTO) {
        log.info("Creating new function: {}", functionDTO.getName());
        try {
            // --- НАЧАЛО КОДА, КОТОРЫЙ НУЖНО ДОБАВИТЬ ---
            // Получаем аутентифицированного пользователя из SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName(); // Получаем имя пользователя

            // Получаем UserDTO из UserService по имени
            // Предполагается, что пользователь всегда существует, если он аутентифицирован
            UUID ownerId = userService.getUserByName(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username))
                    .getId(); // Извлекаем ID из UserDTO

            // Устанавливаем ownerId в DTO перед передачей в Service
            functionDTO.setOwnerId(ownerId);
            // --- КОНЕЦ КОДА, КОТОРЫЙ НУЖНО ДОБАВИТЬ ---

            // Теперь вызываем Service, который уже получит DTO с ownerId
            FunctionDTO createdFunction = functionService.createFunction(functionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFunction);

        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            // Проверяем, не была ли ошибка вызвана не найденным пользователем
            if (e.getMessage().startsWith("User not found: ")) {
                // Возвращаем 400, но можно рассмотреть 401 или 500 в зависимости от контекста
                // 400 подходит, если DTO валидный, но владелец не найден (что странно при Basic Auth)
                // 401 было бы логичнее, если аутентификация сломалась, но здесь она прошла
                // Пусть пока будет 400, как и другие IllegalArgumentException
            }
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating function: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Internal server error"));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @FunctionOwnerOnly
    public ResponseEntity<FunctionDTO> updateFunction(@PathVariable UUID id, @RequestBody FunctionDTO functionDTO) {
        log.info("Updating function with id: {}", id);
        return functionService.updateFunction(id, functionDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/points")
    public ResponseEntity<PointDTO> addPointToFunction(@PathVariable UUID id, @RequestBody PointDTO pointDTO) {
        log.info("Adding point to function with ID: {}", id);

        try {
            PointDTO createdPoint = functionService.addPointToFunction(id, pointDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPoint);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null); // Или вернуть Map с сообщением
        } catch (Exception e) {
            log.error("Error adding point to function: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @FunctionOwnerOnly
    public ResponseEntity<Void> deleteFunction(@PathVariable UUID id) {
        log.info("Deleting function with id: {}", id);
        if (functionService.deleteFunction(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
