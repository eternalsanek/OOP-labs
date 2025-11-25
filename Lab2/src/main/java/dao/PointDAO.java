package dao;

import modelDB.Point;

import java.util.*;

public interface PointDAO {
    void createPoint(UUID funcId, double x, double y);
    Optional<Point> getPointById(UUID id);
    List<Point> getPointsByFunction(UUID funcId);
    void updatePoint(UUID id, double x, double y);
    void deletePoint(UUID id);
    void close();
}