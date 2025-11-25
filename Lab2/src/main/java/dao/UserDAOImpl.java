// UserDAOImpl.java
package dao;

import modelDB.User;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

@Slf4j
public class UserDAOImpl implements UserDAO {
    private final Connection conn;

    public UserDAOImpl(Connection conn) {
        this.conn = conn;
        log.debug("Создан UserDAOImpl с соединением");
    }

    @Override
    public void createUser(String name, String passwordHash, String role) {
        String sql = "INSERT INTO users (name, password_hash, role) VALUES (?, ?, ?)";
        log.debug("SQL запрос создания пользователя: {}", sql);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, passwordHash);
            ps.setString(3, role);
            int affectedRows = ps.executeUpdate();
            log.info("Пользователь создан: имя={}, роль={}, затронуто строк: {}", name, role, affectedRows);
        } catch (SQLException e) {
            log.error("Ошибка при создании пользователя {}: {}", name, e.getMessage(), e);
            throw new RuntimeException("Ошибка создания пользователя", e);
        }
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        log.debug("SQL запрос получения пользователя по ID: {}", id);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = map(rs);
                log.debug("Пользователь найден по ID {}: {}", id, user.getName());
                return Optional.of(user);
            }
            log.debug("Пользователь не найден по ID: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Ошибка при получении пользователя по ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка получения пользователя", e);
        }
    }

    @Override
    public Optional<User> getUserByName(String name) {
        String sql = "SELECT * FROM users WHERE name = ?";
        log.debug("SQL запрос получения пользователя по имени: {}", name);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = map(rs);
                log.debug("Пользователь найден по имени: {}", name);
                return Optional.of(user);
            }
            log.debug("Пользователь не найден по имени: {}", name);
            return Optional.empty();
        } catch (SQLException e) {
            log.error("Ошибка при получении пользователя по имени {}: {}", name, e.getMessage(), e);
            throw new RuntimeException("Ошибка получения пользователя", e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        log.debug("SQL запрос получения всех пользователей");

        List<User> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
            log.debug("Найдено {} пользователей", list.size());
        } catch (SQLException e) {
            log.error("Ошибка при получении всех пользователей: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка получения пользователей", e);
        }
        return list;
    }

    @Override
    public void updateUser(UUID id, String newName, String newPasswordHash, String role) {
        String sql = "UPDATE users SET name = ?, password_hash = ?, role = ? WHERE id = ?";
        log.debug("SQL запрос обновления пользователя: ID={}", id);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setString(2, newPasswordHash);
            ps.setString(3, role);
            ps.setObject(4, id);
            int affectedRows = ps.executeUpdate();
            log.info("Пользователь обновлен: ID={}, новое имя={}, затронуто строк: {}",
                    id, newName, affectedRows);
        } catch (SQLException e) {
            log.error("Ошибка при обновлении пользователя ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка обновления пользователя", e);
        }
    }

    @Override
    public void deleteUser(UUID id) {
        String sql = "DELETE FROM users WHERE id = ?";
        log.debug("SQL запрос удаления пользователя: ID={}", id);

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            int affectedRows = ps.executeUpdate();
            log.info("Пользователь удален: ID={}, затронуто строк: {}", id, affectedRows);
        } catch (SQLException e) {
            log.error("Ошибка при удалении пользователя ID {}: {}", id, e.getMessage(), e);
            throw new RuntimeException("Ошибка удаления пользователя", e);
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

    private User map(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId((UUID) rs.getObject("id"));
        user.setName(rs.getString("name"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        return user;
    }
}