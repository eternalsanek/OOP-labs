package ru.ssau.tk.NAME.PROJECT.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Functions", indexes = {@Index(name = "idx_owner", columnList = "id_owner"), @Index(name = "idx_type", columnList = "type")})
public class Function {
    @Id
    @GeneratedValue
    @Column(name = "ID", columnDefinition = "UUID")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_owner", nullable = false)
    private User owner;
    @Column(name = "Name", nullable = false, length = 255)
    private String name;
    @Column(name = "Type", nullable = false, length = 100)
    private String type;
    @Column(name = "Expression", columnDefinition = "TEXT")
    private String expression;
    @OneToMany(mappedBy = "function", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Point> points = new ArrayList<>();

    public Function() {}
    public Function(User owner, String name, String type, String expression) {
        this.owner = owner;
        this.name = name;
        this.type = type;
        this.expression = expression;
    }
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
    public List<Point> getPoints() { return points; }
    public void setPoints(List<Point> points) { this.points = points; }
    public void addPoint(Point point) {
        points.add(point);
        point.setFunction(this);
    }
    public void removePoint(Point point) {
        points.remove(point);
        point.setFunction(null);
    }
    @Override
    public String toString() {
        return "Function{" + "id=" + id + ", owner=" + (owner != null ? owner.getId() : "null") + ", name='" + name + '\'' + ", type='" + type + '\'' + '}';
    }
}