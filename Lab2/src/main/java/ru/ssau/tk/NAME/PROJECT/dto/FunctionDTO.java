package ru.ssau.tk.NAME.PROJECT.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FunctionDTO {
    private UUID id;
    private String name;
    private String type;
    private String expression;
    private UUID ownerId;
    private List<PointDTO> points;
}
