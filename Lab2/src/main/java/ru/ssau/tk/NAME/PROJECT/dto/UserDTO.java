package ru.ssau.tk.NAME.PROJECT.dto;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private UUID id;
    private String name;
    private String role;
    private String passwordHash;
}
