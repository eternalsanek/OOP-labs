// FunctionDAOImpl.java
package dao;

import modelDB.Function;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

@Slf4j
public class FunctionDAOImpl implements FunctionDAO {
    private final Connection conn;

    public FunctionDAOImpl(Connection conn) {
        this.conn = conn;
        log.debug("Создан FunctionDAOImpl с соединением");
    }

    @Override
    public void createFunction(UUID ownerId, String name, String type, String expression) {
        String sql = "INSERT INTO functions (id_owner, name, type, expression) VALUES (?, ?, ?, ?)";
        log.debug("SQL запрос создания функции: имя={}, тип={}", name, type);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, ownerId);
            ps.setString(2, name);
            ps.setString(3, type);
            ps.setString(4, expression);
            int affectedRows = ps.executeUpdate();
            log.info("Функция создана: имя={}, тип={}, владелец={}, затронуто строк: {}",
                    name, type, ownerId, affectedRows);
        } catch (SQLException e) {
            log.error("Ошибка при создании функции {}: {}", name, e.getMessage(), e);
            throw new RuntimeException("Ошибка создания функции", e);
        }
    }

    @Override
    public Optional<Function> getFunctionById(UUID id) {
        String sql = "SELECT * FROM functions WHERE id = ?";
        log.debug("SQL запрос получения функции по ID: {}", id);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Function function = map(rs);
                log.debug("Функция найдена по ID {}: {}", id, function.getName());
                return Optional.of(function);
            }
            log.debug("Функция не найдена по ID: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Ошибка при получении функции по ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка получения функции", e);
        }
    }

    @Override
    public List<Function> getFunctionsByOwner(UUID ownerId) {
        String sql = "SELECT * FROM functions WHERE id_owner = ? ORDER BY name";
        log.debug("SQL запрос получения функций пользователя: ID={}", ownerId);

        List<Function> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }
            log.debug("Найдено {} функций для пользователя ID={}", list.size(), ownerId);
        } catch (SQLException e) {
            log.error("Ошибка при получении функций пользователя ID {}: {}", ownerId, e.getMessage(), e);
            throw new RuntimeException("Ошибка получения функций", e);
        }
        return list;
    }

    @Override
    public void updateFunction(UUID id, String name, String type, String expression) {
        String sql = "UPDATE functions SET name = ?, type = ?, expression = ? WHERE id = ?";
        log.debug("SQL запрос обновления функции: ID={}", id);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setString(3, expression);
            ps.setObject(4, id);
            int affectedRows = ps.executeUpdate();
            log.info("Функция обновлена: ID={}, новое имя={}, затронуто строк: {}",
                    id, name, affectedRows);
        } catch (SQLException e) {
            log.error("Ошибка при обновлении функции ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка обновления функции", e);
        }
    }

    @Override
    public void deleteFunction(UUID id) {
        String sql = "DELETE FROM functions WHERE id = ?";
        log.debug("SQL запрос удаления функции: ID={}", id);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            int affectedRows = ps.executeUpdate();
            log.info("Функция удалена: ID={}, затронуто строк: {}", id, affectedRows);
        } catch (SQLException e) {
            log.error("Ошибка при удалении функции ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка удаления функции", e);
        }
    }

    @Override
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                log.debug("Соединение PointDAOImpl закрыто");
            }
        } catch (SQLException e) {
            log.error("Ошибка при закрытии соединения PointDAOImpl: {}", e.getMessage(), e);
        }
    }

    private Function map(ResultSet rs) throws SQLException {
        Function function = new Function();
        function.setId((UUID) rs.getObject("id"));
        function.setOwnerId((UUID) rs.getObject("id_owner"));
        function.setName(rs.getString("name"));
        function.setType(rs.getString("type"));
        function.setExpression(rs.getString("expression"));
        return function;
    }
}