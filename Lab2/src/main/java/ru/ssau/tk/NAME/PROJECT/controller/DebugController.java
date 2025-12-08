package ru.ssau.tk.NAME.PROJECT.controller;

import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/debug")
public class DebugController {
    @PostMapping("/test-point")
    public Map<String, Object> testPoint(@RequestBody Map<String, Object> request) {
        log.info("=== DEBUG TEST-POINT ENDPOINT ===");
        log.info("Received request: {}", request);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("request", request);
        if (request.containsKey("functionId")) {
            Object functionId = request.get("functionId");
            response.put("functionId_type", functionId != null ? functionId.getClass().getName() : "null");
            response.put("functionId_value", functionId);
        }
        if (request.containsKey("xVal")) {
            Object xVal = request.get("xVal");
            response.put("xVal_type", xVal != null ? xVal.getClass().getName() : "null");
            response.put("xVal_value", xVal);
            response.put("xVal_isNumber", xVal instanceof Number);
            response.put("xVal_isString", xVal instanceof String);
        }
        if (request.containsKey("yVal")) {
            Object yVal = request.get("yVal");
            response.put("yVal_type", yVal != null ? yVal.getClass().getName() : "null");
            response.put("yVal_value", yVal);
            response.put("yVal_isNumber", yVal instanceof Number);
            response.put("yVal_isString", yVal instanceof String);
        }
        log.info("Response: {}", response);
        return response;
    }

    @PostMapping("/test-point-json")
    public Map<String, Object> testPointJson(@RequestBody String rawJson) {
        log.info("=== DEBUG TEST-POINT-JSON ENDPOINT ===");
        log.info("Received raw JSON: {}", rawJson);
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("rawJson", rawJson);
        response.put("jsonLength", rawJson.length());
        return response;
    }
}
