package dao;

import modelDB.Point;

import java.sql.*;
import java.util.*;

public class PointDAOImpl implements PointDAO {
    private final Connection conn;

    public PointDAOImpl(Connection conn) { this.conn = conn; }

    @Override
    public void createPoint(UUID funcId, double x, double y) {
        String sql = "INSERT INTO points (id_function, x_val, y_val) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, funcId);
            ps.setDouble(2, x);
            ps.setDouble(3, y);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Point> getPointById(UUID id) {
        String sql = "SELECT * FROM points WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<Point> getPointsByFunction(UUID funcId) {
        List<Point> list = new ArrayList<>();
        String sql = "SELECT * FROM points WHERE id_function = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, funcId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public void updatePoint(UUID id, double x, double y) {
        String sql = "UPDATE points SET x_val = ?, y_val = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, x);
            ps.setDouble(2, y);
            ps.setObject(3, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void deletePoint(UUID id) {
        String sql = "DELETE FROM points WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Point map(ResultSet rs) throws SQLException {
        Point p = new Point();
        p.id = (UUID) rs.getObject("id");
        p.functionId = (UUID) rs.getObject("id_function");
        p.xVal = rs.getDouble("x_val");
        p.yVal = rs.getDouble("y_val");
        return p;
    }
}