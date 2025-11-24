package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDTO {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String type;
    private String expression;
}
