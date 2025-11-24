// PointDAOImpl.java
package dao;

import modelDB.Point;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

@Slf4j
public class PointDAOImpl implements PointDAO {
    private final Connection conn;

    public PointDAOImpl(Connection conn) {
        this.conn = conn;
        log.debug("Создан PointDAOImpl с соединением");
    }

    @Override
    public void createPoint(UUID funcId, double x, double y) {
        String sql = "INSERT INTO points (id_function, x_val, y_val) VALUES (?, ?, ?)";
        log.debug("SQL запрос создания точки: функция={}, x={}, y={}", funcId, x, y);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, funcId);
            ps.setDouble(2, x);
            ps.setDouble(3, y);
            int affectedRows = ps.executeUpdate();
            log.info("Точка создана: функция={}, координаты=({}, {}), затронуто строк: {}",
                    funcId, x, y, affectedRows);
        } catch (SQLException e) {
            log.error("Ошибка при создании точки (x={}, y={}): {}", x, y, e.getMessage(), e);
            throw new RuntimeException("Ошибка создания точки", e);
        }
    }

    @Override
    public Optional<Point> getPointById(UUID id) {
        String sql = "SELECT * FROM points WHERE id = ?";
        log.debug("SQL запрос получения точки по ID: {}", id);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Point point = map(rs);
                log.debug("Точка найдена по ID {}: координаты=({}, {})",
                        id, point.getXVal(), point.getYVal());
                return Optional.of(point);
            }
            log.debug("Точка не найдена по ID: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Ошибка при получении точки по ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка получения точки", e);
        }
    }

    @Override
    public List<Point> getPointsByFunction(UUID funcId) {
        String sql = "SELECT * FROM points WHERE id_function = ? ORDER BY x_val";
        log.debug("SQL запрос получения точек функции: ID={}", funcId);

        List<Point> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, funcId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            log.debug("Найдено {} точек для функции ID={}", list.size(), funcId);
        } catch (SQLException e) {
            log.error("Ошибка при получении точек функции ID {}: {}", funcId, e.getMessage(), e);
            throw new RuntimeException("Ошибка получения точек", e);
        }
        return list;
    }

    @Override
    public void updatePoint(UUID id, double x, double y) {
        String sql = "UPDATE points SET x_val = ?, y_val = ? WHERE id = ?";
        log.debug("SQL запрос обновления точки: ID={}", id);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, x);
            ps.setDouble(2, y);
            ps.setObject(3, id);
            int affectedRows = ps.executeUpdate();
            log.info("Точка обновлена: ID={}, новые координаты=({}, {}), затронуто строк: {}",
                    id, x, y, affectedRows);
        } catch (SQLException e) {
            log.error("Ошибка при обновлении точки ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка обновления точки", e);
        }
    }

    @Override
    public void deletePoint(UUID id) {
        String sql = "DELETE FROM points WHERE id = ?";
        log.debug("SQL запрос удаления точки: ID={}", id);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            int affectedRows = ps.executeUpdate();
            log.info("Точка удалена: ID={}, затронуто строк: {}", id, affectedRows);
        } catch (SQLException e) {
            log.error("Ошибка при удалении точки ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка удаления точки", e);
        }
    }

    public void deletePointsByFunction(UUID funcId) {
        String sql = "DELETE FROM points WHERE id_function = ?";
        log.debug("SQL запрос удаления всех точек функции: ID={}", funcId);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, funcId);
            int affectedRows = ps.executeUpdate();
            log.info("Удалены все точки функции: ID={}, удалено точек: {}", funcId, affectedRows);
        } catch (SQLException e) {
            log.error("Ошибка при удалении точек функции ID {}: {}", funcId, e.getMessage(), e);
            throw new RuntimeException("Ошибка удаления точек функции", e);
        }
    }

    private Point map(ResultSet rs) throws SQLException {
        Point point = new Point();
        point.setId((UUID) rs.getObject("id"));
        point.setFunctionId((UUID) rs.getObject("id_function"));
        point.setXVal(rs.getDouble("x_val"));
        point.setYVal(rs.getDouble("y_val"));
        return point;
    }
}