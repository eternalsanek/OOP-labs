package dao;

import modelDB.Function;
import modelDB.Point;
import org.junit.jupiter.api.*;
import testutils.TestDataFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PointDAOTest {

    private static Connection conn;
    private static PointDAO pointDAO;
    private static FunctionDAO functionDAO;
    private static UserDAO userDAO;
    private UUID funcId;

    @BeforeAll
    static void setupAll() {
        conn = ConnectionFactory.getConnection();
        pointDAO = new PointDAOImpl(conn);
        functionDAO = new FunctionDAOImpl(conn);
        userDAO = new UserDAOImpl(conn);
    }

    @BeforeEach
    void setupFunction() throws Exception {
        conn.createStatement().execute("TRUNCATE TABLE points, functions, users RESTART IDENTITY CASCADE");

        // создаём владельца функции
        String name = TestDataFactory.randomUserName();
        String password = TestDataFactory.randomPassword();
        String role = "user";
        userDAO.createUser(name, password, role);
        UUID ownerId = userDAO.getUserByName(name).get().getId();

        // создаём функцию
        String funcName = TestDataFactory.randomFunctionName();
        functionDAO.createFunction(ownerId, funcName, "MathFunction", "x^2 + 1");
        funcId = functionDAO.getFunctionsByOwner(ownerId).get(0).getId();
    }

    @Test
    void testCreateReadUpdateDeletePoint() {
        double x = TestDataFactory.randomCoordinate();
        double y = TestDataFactory.randomCoordinate();

        // CREATE
        pointDAO.createPoint(funcId, x, y);

        List<Point> points = pointDAO.getPointsByFunction(funcId);
        assertEquals(1, points.size());
        Point p = points.get(0);
        assertEquals(x, p.getXVal(), 10e-9);
        assertEquals(y, p.getYVal(), 10e-9);

        // UPDATE
        double newX = TestDataFactory.randomCoordinate();
        double newY = TestDataFactory.randomCoordinate();
        pointDAO.updatePoint(p.getId(), newX, newY);

        Optional<Point> updated = pointDAO.getPointById(p.getId());
        assertTrue(updated.isPresent());
        assertEquals(newX, updated.get().getXVal(), 10e-9);
        assertEquals(newY, updated.get().getYVal(), 10e-9);

        // DELETE
        pointDAO.deletePoint(p.getId());
        Optional<Point> deleted = pointDAO.getPointById(p.getId());
        assertFalse(deleted.isPresent());
    }

    @Test
    void testDeletePointsByFunction() {
        // создаём несколько точек
        for (int i = 0; i < 5; i++) {
            pointDAO.createPoint(funcId, TestDataFactory.randomCoordinate(), TestDataFactory.randomCoordinate());
        }
        List<Point> points = pointDAO.getPointsByFunction(funcId);
        assertEquals(5, points.size());

        // DELETE all
        ((PointDAOImpl) pointDAO).deletePointsByFunction(funcId);
        points = pointDAO.getPointsByFunction(funcId);
        assertEquals(0, points.size());
    }
}
