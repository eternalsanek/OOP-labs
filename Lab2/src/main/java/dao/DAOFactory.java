package dao;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class DAOFactory {

    public static UserDAO getUserDAO() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            log.debug("Создан UserDAO с новым соединением");
            return new UserDAOImpl(connection);
        } catch (SQLException e) {
            log.error("Ошибка создания UserDAO: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать UserDAO", e);
        }
    }

    public static FunctionDAO getFunctionDAO() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            log.debug("Создан FunctionDAO с новым соединением");
            return new FunctionDAOImpl(connection);
        } catch (SQLException e) {
            log.error("Ошибка создания FunctionDAO: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать FunctionDAO", e);
        }
    }

    public static PointDAO getPointDAO() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            log.debug("Создан PointDAO с новым соединением");
            return new PointDAOImpl(connection);
        } catch (SQLException e) {
            log.error("Ошибка создания PointDAO: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось создать PointDAO", e);
        }
    }
}
