// PointDTO.java
package modelDTO;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointDTO {
    private UUID id;
    private UUID functionId;
    private double x;
    private double y;
}
