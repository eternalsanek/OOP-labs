package listener;

import dao.DAOFactory;
import dao.DatabaseConnection;
import lombok.extern.slf4j.Slf4j;
import modelDB.User;
import service.DTOTransformService;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.Statement;

@Slf4j
@WebListener
public class DatabaseInitializerListener implements ServletContextListener {

    private DTOTransformService service;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        log.info("=== Запуск инициализации приложения ===");

        service = new DTOTransformService(
                DAOFactory.getUserDAO(),
                DAOFactory.getFunctionDAO(),
                DAOFactory.getPointDAO()
        );

        recreateTables();
        createTestData();

        log.info("=== База данных успешно инициализирована ===");
    }

    private void recreateTables() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS points CASCADE");
            stmt.execute("DROP TABLE IF EXISTS functions CASCADE");
            stmt.execute("DROP TABLE IF EXISTS users CASCADE");

            stmt.execute("""
            CREATE TABLE users (
                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                name VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                role VARCHAR(50) NOT NULL CHECK (role IN ('USER', 'ADMIN'))
            )
            """);

            stmt.execute("""
            CREATE TABLE functions (
                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                id_owner UUID REFERENCES users(id) ON DELETE CASCADE,
                name VARCHAR(255) NOT NULL,
                type VARCHAR(50) NOT NULL CHECK (type IN ('MathFunction', 'TabulatedFunction')),
                expression TEXT
            )
            """);

            stmt.execute("""
            CREATE TABLE points (
                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                id_function UUID REFERENCES functions(id) ON DELETE CASCADE,
                x_val DOUBLE PRECISION NOT NULL,
                y_val DOUBLE PRECISION NOT NULL
            )
            """);

            log.info("Таблицы успешно пересозданы");
        } catch (Exception e) {
            log.error("Ошибка при пересоздании таблиц", e);
            throw new RuntimeException("Не удалось инициализировать базу данных", e);
        }
    }

    private void createTestData() {
        // === Пользователи ===
        User admin = service.createUser("admin", "admin123", "ADMIN");
        User alice = service.createUser("alice", "alicepass", "USER");
        User bob = service.createUser("bob", "bobpass", "USER");
        User charlie = service.createUser("charlie", "charliepass", "USER");
        User dana = service.createUser("dana", "danapass", "USER");

        // === Функции ===
        var f1 = service.createFunction(alice.getId(), "Квадратичная", "MathFunction", "x^2 + 2x + 1");
        var f2 = service.createFunction(bob.getId(), "Линейная", "MathFunction", "3x + 5");
        var f3 = service.createFunction(alice.getId(), "Синус табулированный", "TabulatedFunction", null);
        var f4 = service.createFunction(charlie.getId(), "Кубическая", "MathFunction", "x^3 - 3x + 2");
        var f5 = service.createFunction(dana.getId(), "Косинус табулированный", "TabulatedFunction", null);

        // === Точки для f1 (квадратичная) ===
        service.createPoint(f1.getId(), -2.0, 1.0);
        service.createPoint(f1.getId(), -1.0, 0.0);
        service.createPoint(f1.getId(), 0.0, 1.0);
        service.createPoint(f1.getId(), 1.0, 4.0);
        service.createPoint(f1.getId(), 2.0, 9.0);

        // === Точки для f2 (линейная) ===
        service.createPoint(f2.getId(), -2.0, -1.0);
        service.createPoint(f2.getId(), 0.0, 5.0);
        service.createPoint(f2.getId(), 2.0, 11.0);
        service.createPoint(f2.getId(), 4.0, 17.0);

        // === Точки для f3 (синус табулированный) ===
        service.createPoint(f3.getId(), 0.0, 0.0);
        service.createPoint(f3.getId(), 1.57, 1.0);
        service.createPoint(f3.getId(), 3.14, 0.0);
        service.createPoint(f3.getId(), 4.71, -1.0);
        service.createPoint(f3.getId(), 6.28, 0.0);

        // === Точки для f4 (кубическая) ===
        service.createPoint(f4.getId(), -2.0, -2.0);
        service.createPoint(f4.getId(), -1.0, 4.0);
        service.createPoint(f4.getId(), 0.0, 2.0);
        service.createPoint(f4.getId(), 1.0, 0.0);
        service.createPoint(f4.getId(), 2.0, 4.0);

        // === Точки для f5 (косинус табулированный) ===
        service.createPoint(f5.getId(), 0.0, 1.0);
        service.createPoint(f5.getId(), 1.57, 0.0);
        service.createPoint(f5.getId(), 3.14, -1.0);
        service.createPoint(f5.getId(), 4.71, 0.0);
        service.createPoint(f5.getId(), 6.28, 1.0);

        // === Динамически создаём 10 пользователей и их функции ===
        for (int i = 1; i <= 10; i++) {
            String username = "user" + i;
            String password = "pass" + i;
            User user = service.createUser(username, password, "USER");

            // Создаём MathFunction
            String polyExpression = i + "x^2 + " + (2*i) + "x + " + i;
            var mathFunc = service.createFunction(user.getId(), "PolyFunction" + i, "MathFunction", polyExpression);

            // Несколько точек для многочлена
            for (int x = -2; x <= 2; x++) {
                double y = i * x * x + 2*i*x + i;
                service.createPoint(mathFunc.getId(), (double)x, y);
            }

            // Создаём TabulatedFunction
            var tabFunc = service.createFunction(user.getId(), "TabulatedFunction" + i, "TabulatedFunction", null);
            // Несколько точек для табулированной функции
            for (int j = 0; j <= 5; j++) {
                double xVal = j * 1.0;
                double yVal = Math.sin(xVal + i);
                service.createPoint(tabFunc.getId(), xVal, yVal);
            }
        }

        log.info("Тестовые данные успешно добавлены");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (service != null) service.close();
        log.info("Приложение остановлено");
    }
}