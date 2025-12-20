package ru.ssau.tk.NAME.PROJECT.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.ssau.tk.NAME.PROJECT.dto.FunctionDTO;
import ru.ssau.tk.NAME.PROJECT.service.FunctionService;
import ru.ssau.tk.NAME.PROJECT.security.OwnerOnly;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/functions")
@RequiredArgsConstructor
public class FunctionController {

    private final FunctionService functionService;

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
    public ResponseEntity<FunctionDTO> createFunction(@RequestBody FunctionDTO functionDTO) {
        log.info("Creating new function: {}", functionDTO.getName());
        FunctionDTO createdFunction = functionService.createFunction(functionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFunction);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @OwnerOnly
    public ResponseEntity<FunctionDTO> updateFunction(@PathVariable UUID id, @RequestBody FunctionDTO functionDTO) {
        log.info("Updating function with id: {}", id);
        return functionService.updateFunction(id, functionDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN', 'MODERATOR')")
    @OwnerOnly
    public ResponseEntity<Void> deleteFunction(@PathVariable UUID id) {
        log.info("Deleting function with id: {}", id);
        if (functionService.deleteFunction(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
