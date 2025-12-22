package ru.ssau.tk.NAME.PROJECT.security;

import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import ru.ssau.tk.NAME.PROJECT.exceptions.AccessDeniedException;
import ru.ssau.tk.NAME.PROJECT.repository.FunctionRepository;
import ru.ssau.tk.NAME.PROJECT.repository.PointRepository;
import ru.ssau.tk.NAME.PROJECT.repository.UserRepository;
import ru.ssau.tk.NAME.PROJECT.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final PointRepository pointRepository;

    @Before("@annotation(ru.ssau.tk.NAME.PROJECT.security.FunctionOwnerOnly) && args(id, ..)")
    public void checkOwnerForFunction(UUID id) {
        log.debug("Checking ownership for function with ID: {}", id);

        User currentUser = getUserFromAuth();
        if (currentUser == null) {
            log.warn("No authenticated user found");
            throw new AccessDeniedException("AUTHENTICATED");
        }

        Function function = functionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Function not found with ID: {}", id);
                    return new IllegalArgumentException("Function not found");
                });

        log.debug("Current user: {}, Function owner: {}",
                currentUser.getId(), function.getOwner().getId());

        if (!function.getOwner().getId().equals(currentUser.getId())
                && !hasRole("ADMIN")
                && !hasRole("MODERATOR")) {
            log.warn("Access denied: User {} tried to access function owned by {}",
                    currentUser.getId(), function.getOwner().getId());
            throw new AccessDeniedException("OWNER or ADMIN");
        }

        log.debug("Access granted for function: {}", id);
    }

    @Before("@annotation(ru.ssau.tk.NAME.PROJECT.security.PointOwnerOnly) && args(pointId, ..)")
    public void checkOwnerForPoint(UUID pointId) {
        log.debug("Checking ownership for point with ID: {}", pointId);

        User currentUser = getUserFromAuth();
        if (currentUser == null) {
            throw new AccessDeniedException("AUTHENTICATED");
        }

        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new IllegalArgumentException("Point not found"));

        if (!point.getFunction().getOwner().getId().equals(currentUser.getId())
                && !hasRole("ADMIN")
                && !hasRole("MODERATOR")) {
            log.warn("Access denied: User {} tried to access point owned by {}",
                    currentUser.getId(), point.getFunction().getOwner().getId());
            throw new AccessDeniedException("OWNER or ADMIN");
        }
    }

    private User getUserFromAuth() {
        if (authService.getCurrentUser() != null) {
            return userRepository.findById(authService.getCurrentUser().getId())
                    .orElse(null);
        }
        return null;
    }

    private boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication(); // Изменено имя переменной
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals("ROLE_" + role)); // Изменено имя переменной
    }
}
