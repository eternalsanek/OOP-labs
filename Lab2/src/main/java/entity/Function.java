package entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "functions", indexes = {
        @Index(name = "idx_owner", columnList = "id_owner"),
        @Index(name = "idx_type", columnList = "type")
})
public class Function {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_owner", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @Column(name = "expression", columnDefinition = "TEXT")
    private String expression;

    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CodePoint> codePoints = new ArrayList<>();

    // Конструкторы
    public Function() {}

    public Function(User owner, String name, String type, String expression) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.expression = expression;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }

    public List<CodePoint> getCodePoints() { return codePoints; }
    public void setCodePoints(List<CodePoint> codePoints) { this.codePoints = codePoints; }

    // Вспомогательные методы
    public void addCodePoint(CodePoint codePoint) {
        codePoints.add(codePoint);
        codePoint.setFunction(this);
    }

    public void removeCodePoint(CodePoint codePoint) {
        codePoints.remove(codePoint);
        codePoint.setFunction(null);
    }

    @Override
    public String toString() {
        return "Function{" +
                "id=" + id +
                ", owner=" + (owner != null ? owner.getId() : "null") +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}