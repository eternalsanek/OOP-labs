package dao;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/labOOP_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "123456";

    static {
        try {
            Class.forName("org.postgresql.Driver");
            log.info("Драйвер PostgreSQL успешно загружен");
        } catch (ClassNotFoundException e) {
            log.error("Ошибка загрузки драйвера PostgreSQL: {}", e.getMessage(), e);
            throw new RuntimeException("Драйвер PostgreSQL не найден", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        log.debug("Установление соединения с БД: {}", URL);
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        log.info("Соединение с БД успешно установлено");
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                log.debug("Соединение с БД закрыто");
            } catch (SQLException e) {
                log.error("Ошибка при закрытии соединения: {}", e.getMessage(), e);
            }
        }
    }

    public static void testConnection() {
        log.info("Тестирование соединения с БД...");
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                log.info("Соединение с БД успешно протестировано");
            }
        } catch (SQLException e) {
            log.error("Ошибка тестирования соединения: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось подключиться к БД", e);
        }
    }
}
