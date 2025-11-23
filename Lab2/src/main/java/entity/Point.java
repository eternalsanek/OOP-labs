package entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "points", indexes = {@Index(name = "idx_function", columnList = "id_function"), @Index(name = "idx_x_val", columnList = "x_val")}, uniqueConstraints = @UniqueConstraint(name = "unique_function_x_val", columnNames = {"id_function", "x_val"}))
public class Point {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "UUID")
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_function", nullable = false)
    private Function function;
    @Column(name = "x_val", nullable = false, precision = 19, scale = 10)
    private BigDecimal xVal;
    @Column(name = "y_val", nullable = false, precision = 19, scale = 10)
    private BigDecimal yVal;


    public Point() {}
    public Point(Function function, BigDecimal xVal, BigDecimal yVal) {
        this.function = function;
        this.xVal = xVal;
        this.yVal = yVal;
    }
    public Point(Function function, double xVal, double yVal) {
        this.function = function;
        this.xVal = BigDecimal.valueOf(xVal);
        this.yVal = BigDecimal.valueOf(yVal);
    }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Function getFunction() { return function; }
    public void setFunction(Function function) { this.function = function; }
    public BigDecimal getXVal() { return xVal; }
    public void setXVal(BigDecimal xVal) { this.xVal = xVal; }
    public BigDecimal getYVal() { return yVal; }
    public void setYVal(BigDecimal yVal) { this.yVal = yVal; }
    @Override
    public String toString() {
        return "CodePoint{" + "id=" + id + ", function=" + (function != null ? function.getId() : "null") + ", xVal=" + xVal + ", yVal=" + yVal + '}';
    }
}
