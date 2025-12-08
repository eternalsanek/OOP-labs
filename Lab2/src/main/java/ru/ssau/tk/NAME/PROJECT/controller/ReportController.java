package ru.ssau.tk.NAME.PROJECT.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Value("${reports.dir:/app/reports}")
    private String reportsDir;

    @PostMapping("/performance")
    public ResponseEntity<Map<String, Object>> savePerformanceReport(
            @RequestBody Map<String, Object> reportData) {

        try {
            log.info("Saving performance report to Docker volume");

            // Создаем директорию для отчетов
            Path reportsPath = Paths.get(reportsDir);
            if (!Files.exists(reportsPath)) {
                Files.createDirectories(reportsPath);
                log.info("Created reports directory: {}", reportsPath);
            }

            // Генерируем имя файла с временной меткой
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));

            Map<String, String> savedFiles = new HashMap<>();

            // 1. Сохраняем CSV отчет
            if (reportData.containsKey("csv")) {
                String csvContent = (String) reportData.get("csv");
                String csvFilename = String.format("performance_%s.csv", timestamp);
                Path csvPath = reportsPath.resolve(csvFilename);

                Files.writeString(csvPath, csvContent, StandardOpenOption.CREATE);
                savedFiles.put("csv", csvFilename);
                log.info("CSV report saved: {}", csvPath);
            }

            // 2. Сохраняем JSON отчет
            String jsonFilename = String.format("performance_%s.json", timestamp);
            Path jsonPath = reportsPath.resolve(jsonFilename);
            String jsonContent = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(reportData);

            Files.writeString(jsonPath, jsonContent, StandardOpenOption.CREATE);
            savedFiles.put("json", jsonFilename);

            // 3. Сохраняем HTML отчет (опционально)
            if (reportData.containsKey("html")) {
                String htmlContent = (String) reportData.get("html");
                String htmlFilename = String.format("performance_%s.html", timestamp);
                Path htmlPath = reportsPath.resolve(htmlFilename);

                Files.writeString(htmlPath, htmlContent, StandardOpenOption.CREATE);
                savedFiles.put("html", htmlFilename);
            }

            // 4. Создаем summary файл
            String summary = createSummaryReport(reportData, timestamp);
            String summaryFilename = String.format("summary_%s.txt", timestamp);
            Path summaryPath = reportsPath.resolve(summaryFilename);
            Files.writeString(summaryPath, summary, StandardOpenOption.CREATE);

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Reports saved to Docker volume");
            response.put("timestamp", timestamp);
            response.put("files", savedFiles);
            response.put("path", reportsPath.toString());
            response.put("hostPath", "./reports/"); // Относительный путь на хосте

            log.info("Performance reports saved successfully. Files: {}", savedFiles);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to save performance report", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Failed to save report: " + e.getMessage());
            errorResponse.put("error", e.getClass().getName());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> listReports() {
        try {
            Path reportsPath = Paths.get(reportsDir);

            if (!Files.exists(reportsPath)) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "empty");
                response.put("message", "No reports directory found");
                response.put("path", reportsDir);
                return ResponseEntity.ok(response);
            }

            Map<String, Object> fileList = new HashMap<>();
            Files.list(reportsPath)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {
                        try {
                            Map<String, Object> fileInfo = new HashMap<>();
                            fileInfo.put("name", path.getFileName().toString());
                            fileInfo.put("size", Files.size(path));
                            fileInfo.put("modified", Files.getLastModifiedTime(path).toString());
                            fileList.put(path.getFileName().toString(), fileInfo);
                        } catch (IOException e) {
                            log.warn("Could not get file info for {}", path);
                        }
                    });

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("count", fileList.size());
            response.put("files", fileList);
            response.put("path", reportsPath.toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    private String createSummaryReport(Map<String, Object> reportData, String timestamp) {
        StringBuilder summary = new StringBuilder();
        summary.append("=".repeat(80)).append("\\n");
        summary.append("API PERFORMANCE TEST SUMMARY\\n");
        summary.append("=".repeat(80)).append("\\n\\n");

        summary.append("Test Date: ").append(LocalDateTime.now()).append("\\n");
        summary.append("Timestamp: ").append(timestamp).append("\\n");
        summary.append("Environment: Docker Container\\n");
        summary.append("Reports Path: ").append(reportsDir).append("\\n\\n");

        if (reportData.containsKey("statistics")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> stats = (Map<String, Object>) reportData.get("statistics");
            summary.append("STATISTICS:\\n");
            summary.append("-".repeat(40)).append("\\n");
            stats.forEach((key, value) -> {
                summary.append(key).append(": ").append(value).append("\\n");
            });
        }

        summary.append("\\nFiles saved:\\n");
        summary.append("- performance_").append(timestamp).append(".csv\\n");
        summary.append("- performance_").append(timestamp).append(".json\\n");
        summary.append("- summary_").append(timestamp).append(".txt\\n");

        summary.append("\\n").append("=".repeat(80)).append("\\n");

        return summary.toString();
    }
}
