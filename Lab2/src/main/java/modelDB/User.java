package modelDB;

import java.util.UUID;
import lombok.Data;

@Data
public class User {
    public UUID id;
    public String name;
    public String passwordHash;
    public String role;
}