package entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "ID", columnDefinition = "UUID")
    private UUID id;
    @Column(name = "Name", nullable = false, length = 255)
    private String name;
    @Column(name = "Password_Hash", nullable = false, length = 255)
    private String passwordHash;

    public User(){}
    public User(String name, String passwordHash){
        this.name = name;
        this.passwordHash = passwordHash;
    }
    public UUID getId(){
        return id;
    }
    public void setId(UUID id){
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getPasswordHash(){
        return  passwordHash;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    @Override
    public String toString(){
        return "User{"+"id: "+id+", name: "+name+'\''+'}';
    }
}
