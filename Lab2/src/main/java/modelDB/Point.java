package modelDB;

import java.util.UUID;
import lombok.Data;

@Data
public class Point {
    public UUID id;
    public UUID functionId;
    public double xVal;
    public double yVal;
}
