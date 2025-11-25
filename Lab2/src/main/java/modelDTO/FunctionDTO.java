// FunctionDTO.java
package modelDTO;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FunctionDTO {
    private UUID id;
    private String name;
    private String type; // MathFunction или TabulatedFunction
    private String expression;
    private UUID ownerId;
}
