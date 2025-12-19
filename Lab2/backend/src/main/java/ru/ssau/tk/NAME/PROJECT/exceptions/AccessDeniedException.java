package ru.ssau.tk.NAME.PROJECT.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {
    private final String requiredRole;

    public AccessDeniedException(String requiredRole) {
        super("Access denied. Required role: " + requiredRole);
        this.requiredRole = requiredRole;
    }
}
