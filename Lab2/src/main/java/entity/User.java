package entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "Users")
public class User {
    public enum Role {
        ADMIN, MODERATOR, USER, GUEST
    }
    @Id
    @GeneratedValue
    @Column(name = "ID", columnDefinition = "UUID")
    private UUID id;
    @Column(name = "Name", nullable = false, length = 255)
    private String name;
    @Column(name = "Password_Hash", nullable = false, length = 255)
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false, length = 20)
    private Role role = Role.USER;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Function> functions = new ArrayList<>();

    public User() {
    }
    public User(String name, String passwordHash, Role role) {
        this.name = name;
        this.passwordHash = passwordHash;
        this.role = role;
    }
    public User(String name, String passwordHash) {
        this.name = name;
        this.passwordHash = passwordHash;
        this.role = Role.USER;
    }
    public UUID getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getPasswordHash() {
        return passwordHash;
    }
    public Role getRole() {
        return role;
    }
    public List<Function> getFunctions() {
        return functions;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public void setFunctions(List<Function> functions) {
        this.functions = functions;
    }
    public boolean isAdmin() {
        return Role.ADMIN.equals(role);
    }
    public boolean isModerator() {
        return Role.MODERATOR.equals(role) || isAdmin();
    }
    public boolean canManageFunctions() {
        return isModerator() || Role.USER.equals(role);
    }
    public boolean canViewOnly() {
        return Role.GUEST.equals(role);
    }
    public boolean canEditUser(User targetUser) {
        return isAdmin() || (isModerator() && !targetUser.isAdmin());
    }
    public boolean canDeleteUser(User targetUser) {
        return isAdmin() && !targetUser.equals(this);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(name, user.name) &&
                Objects.equals(passwordHash, user.passwordHash) &&
                role == user.role;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name, passwordHash, role);
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", role=" + role +
                '}';
    }
}
