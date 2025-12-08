package ru.ssau.tk.NAME.PROJECT.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "<h1>LabOOP API is running!</h1>" +
                "<h2>Available endpoints:</h2>" +
                "<ul>" +
                "<li><a href=\"/api/v1/users\">/api/v1/users</a> - Users API</li>" +
                "<li><a href=\"/api/v1/functions\">/api/v1/functions</a> - Functions API</li>" +
                "<li><a href=\"/api/v1/points\">/api/v1/points</a> - Points API</li>" +
                "<li><a href=\"/actuator/health\">/actuator/health</a> - Health check</li>" +
                "<li><a href=\"/actuator\">/actuator</a> - All Actuator endpoints</li>" +
                "</ul>" +
                "<h2>Test endpoints:</h2>" +
                "<ul>" +
                "<li><a href=\"/api/test\">/api/test</a> - Simple test</li>" +
                "<li><a href=\"/api/debug\">/api/debug</a> - Debug info</li>" +
                "</ul>";
    }
}
