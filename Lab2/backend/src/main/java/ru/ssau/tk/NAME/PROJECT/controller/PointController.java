package ru.ssau.tk.NAME.PROJECT.controller;

import ru.ssau.tk.NAME.PROJECT.dto.PointDTO;
import ru.ssau.tk.NAME.PROJECT.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @GetMapping
    public ResponseEntity<List<PointDTO>> getAllPoints() {
        log.info("Getting all points");
        List<PointDTO> points = pointService.getAllPoints();
        log.info("Found {} points", points.size());
        return ResponseEntity.ok(points);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PointDTO> getPointById(@PathVariable UUID id) {
        log.info("Getting point with id: {}", id);
        return pointService.getPointById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createPoint(@RequestBody Map<String, Object> requestData) {
        log.info("Creating new point with data: {}", requestData);

        try {
            String functionIdStr = (String) requestData.get("functionId");
            if (functionIdStr == null) {
                return ResponseEntity.badRequest().body("Function ID is required");
            }
            UUID functionId;
            try {
                functionId = UUID.fromString(functionIdStr);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid Function ID format");
            }
            BigDecimal xVal = parseBigDecimal(requestData.get("xVal"));
            BigDecimal yVal = parseBigDecimal(requestData.get("yVal"));

            log.info("Parsed values - functionId: {}, xVal: {}, yVal: {}", functionId, xVal, yVal);
            PointDTO pointDTO = PointDTO.builder()
                    .functionId(functionId)
                    .xVal(xVal)
                    .yVal(yVal)
                    .build();

            PointDTO createdPoint = pointService.createPoint(pointDTO);
            log.info("Point created successfully with id: {}", createdPoint.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPoint);

        } catch (IllegalArgumentException e) {
            log.error("Validation error creating point: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating point: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating point: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePoint(@PathVariable UUID id, @RequestBody Map<String, Object> requestData) {
        log.info("Updating point with id: {}", id);

        try {
            PointDTO pointDTO = new PointDTO();
            pointDTO.setId(id);

            if (requestData.containsKey("functionId")) {
                String functionIdStr = (String) requestData.get("functionId");
                pointDTO.setFunctionId(UUID.fromString(functionIdStr));
            }

            if (requestData.containsKey("xVal")) {
                pointDTO.setXVal(parseBigDecimal(requestData.get("xVal")));
            }

            if (requestData.containsKey("yVal")) {
                pointDTO.setYVal(parseBigDecimal(requestData.get("yVal")));
            }

            return pointService.updatePoint(id, pointDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error updating point {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating point: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePoint(@PathVariable UUID id) {
        log.info("Deleting point with id: {}", id);

        try {
            if (pointService.deletePoint(id)) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting point {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting point: " + e.getMessage());
        }
    }

    private BigDecimal parseBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }

        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number) value).doubleValue());
        }

        if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                log.warn("Cannot parse BigDecimal from string: '{}', using zero", value);
                return BigDecimal.ZERO;
            }
        }

        log.warn("Unknown value type for BigDecimal: {}, using zero", value.getClass());
        return BigDecimal.ZERO;
    }
}
