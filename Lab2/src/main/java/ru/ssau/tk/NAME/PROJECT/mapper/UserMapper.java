package ru.ssau.tk.NAME.PROJECT.mapper;

import ru.ssau.tk.NAME.PROJECT.dto.UserDTO;
import ru.ssau.tk.NAME.PROJECT.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .passwordHash(user.getPasswordHash())  // Добавляем
                .build();
    }

    public User toEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        if (dto.getRole() != null) {
            try {
                user.setRole(User.Role.valueOf(dto.getRole()));
            } catch (IllegalArgumentException e) {
                user.setRole(User.Role.USER);
            }
        } else {
            user.setRole(User.Role.USER);
        }
        if (dto.getPasswordHash() != null && !dto.getPasswordHash().isEmpty()) {
            user.setPasswordHash(dto.getPasswordHash());
        } else {
            user.setPasswordHash("default_password_" + java.util.UUID.randomUUID().toString().substring(0, 8));
        }
        return user;
    }
}
