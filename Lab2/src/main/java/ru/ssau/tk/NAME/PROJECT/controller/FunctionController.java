package ru.ssau.tk.NAME.PROJECT.controller;

import ru.ssau.tk.NAME.PROJECT.dto.FunctionDTO;
import ru.ssau.tk.NAME.PROJECT.service.FunctionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<FunctionDTO>> getAllFunctions() {
        log.info("Getting all functions");
        List<FunctionDTO> functions = functionService.getAllFunctions();
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FunctionDTO> getFunctionById(@PathVariable UUID id) {
        log.info("Getting function with id: {}", id);
        return functionService.getFunctionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<FunctionDTO>> getFunctionsByOwnerId(@PathVariable UUID ownerId) {
        log.info("Getting functions for owner with id: {}", ownerId);
        List<FunctionDTO> functions = functionService.getFunctionsByOwnerId(ownerId);
        return ResponseEntity.ok(functions);
    }

    @PostMapping
    public ResponseEntity<FunctionDTO> createFunction(@RequestBody FunctionDTO functionDTO) {
        log.info("Creating new function: {}", functionDTO.getName());
        FunctionDTO createdFunction = functionService.createFunction(functionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFunction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunctionDTO> updateFunction(@PathVariable UUID id, @RequestBody FunctionDTO functionDTO) {
        log.info("Updating function with id: {}", id);
        return functionService.updateFunction(id, functionDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable UUID id) {
        log.info("Deleting function with id: {}", id);
        if (functionService.deleteFunction(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
