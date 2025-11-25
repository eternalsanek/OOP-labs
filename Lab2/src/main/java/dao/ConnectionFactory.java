package dao;

import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@Slf4j
public class ConnectionFactory {

    public static Connection getConnection() {
        try {
            log.debug("Загрузка свойств базы данных из application.properties");
            Properties props = new Properties();
            props.load(ConnectionFactory.class.getClassLoader()
                    .getResourceAsStream("application.properties"));

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            log.debug("Попытка соединения с базой данных: {}", url);
            Connection connection = DriverManager.getConnection(url, user, password);
            log.debug("Соединение с базой данных установлено успешно");

            return connection;
        } catch (Exception e) {
            log.error("Не удалось установить соединение с базой данных: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при подключении к базе данных", e);
        }
    }
}