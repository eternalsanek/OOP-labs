package ru.ssau.tk.NAME.PROJECT.repository;

import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PointRepository extends JpaRepository<Point, UUID> {

    List<Point> findByFunction(Function function);

    @Query("SELECT p FROM Point p WHERE p.function.id = :functionId")
    List<Point> findByFunctionId(@Param("functionId") UUID functionId);

    @Query("SELECT p FROM Point p WHERE p.function = :function AND p.xVal BETWEEN :minX AND :maxX")
    List<Point> findByFunctionAndXValBetween(@Param("function") Function function,
                                             @Param("minX") BigDecimal minX,
                                             @Param("maxX") BigDecimal maxX);

    @Query("SELECT p FROM Point p WHERE p.function.owner.id = :ownerId")
    List<Point> findByOwnerId(@Param("ownerId") UUID ownerId);

    @Query("SELECT p FROM Point p WHERE p.function = :function AND p.xVal = :xVal")
    Optional<Point> findByFunctionAndXval(@Param("function") Function function, @Param("xVal") BigDecimal xVal);

    @Query("SELECT COUNT(p) FROM Point p WHERE p.function = :function")
    long countByFunction(@Param("function") Function function);

    void deleteByFunction(Function function);
}
