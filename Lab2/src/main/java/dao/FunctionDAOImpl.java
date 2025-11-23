package dao;

import modelDB.Function;

import java.sql.*;
import java.util.*;

public class FunctionDAOImpl implements FunctionDAO {
    private final Connection conn;

    public FunctionDAOImpl(Connection conn) { this.conn = conn; }

    @Override
    public void createFunction(UUID ownerId, String name, String type, String expression) {
        String sql = "INSERT INTO functions (id_owner, name, type, expression) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, ownerId);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setString(4, expression);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Function> getFunctionById(UUID id) {
        String sql = "SELECT * FROM functions WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public List<Function> getFunctionsByOwner(UUID ownerId) {
        List<Function> list = new ArrayList<>();
        String sql = "SELECT * FROM functions WHERE id_owner = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public void updateFunction(UUID id, String name, String type, String expression) {
        String sql = "UPDATE functions SET name = ?, type = ?, expression = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setString(3, expression);
            ps.setObject(4, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void deleteFunction(UUID id) {
        String sql = "DELETE FROM functions WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Function map(ResultSet rs) throws SQLException {
        Function f = new Function();
        f.id = (UUID) rs.getObject("id");
        f.ownerId = (UUID) rs.getObject("id_owner");
        f.name = rs.getString("name");
        f.type = rs.getString("type");
        f.expression = rs.getString("expression");
        return f;
    }
}