package dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointCreateDTO {
    private UUID functionId;
    private double xVal;
    private double yVal;
}
