package dao;

import modelDB.Function;
import org.junit.jupiter.api.*;
import testutils.TestDataFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FunctionDAOTest {

    private static Connection conn;
    private static FunctionDAO functionDAO;
    private static UserDAO userDAO;
    private UUID ownerId;

    @BeforeAll
    static void setupAll() {
        conn = ConnectionFactory.getConnection();
        functionDAO = new FunctionDAOImpl(conn);
        userDAO = new UserDAOImpl(conn);
    }

    @BeforeEach
    void cleanDatabase() throws Exception {
        conn.createStatement().execute("TRUNCATE TABLE points, functions, users RESTART IDENTITY CASCADE");
        // создаём пользователя-владельца функции
        String name = TestDataFactory.randomUserName();
        String password = TestDataFactory.randomPassword();
        String role = "user";
        userDAO.createUser(name, password, role);
        ownerId = userDAO.getUserByName(name).get().getId();
    }

    @Test
    void testCreateReadUpdateDeleteFunction() {
        String name = TestDataFactory.randomFunctionName();
        String type = TestDataFactory.randomFunctionType();
        String expr = TestDataFactory.randomExpression(type);

        // CREATE
        functionDAO.createFunction(ownerId, name, type, expr);

        List<Function> functions = functionDAO.getFunctionsByOwner(ownerId);
        assertEquals(1, functions.size());
        Function func = functions.get(0);
        assertEquals(name, func.getName());

        // UPDATE
        String newName = TestDataFactory.randomFunctionName();
        String newType = TestDataFactory.randomFunctionType();
        String newExpr = TestDataFactory.randomExpression(newType);
        functionDAO.updateFunction(func.getId(), newName, newType, newExpr);

        Optional<Function> updated = functionDAO.getFunctionById(func.getId());
        assertTrue(updated.isPresent());
        assertEquals(newName, updated.get().getName());

        // DELETE
        functionDAO.deleteFunction(func.getId());
        Optional<Function> deleted = functionDAO.getFunctionById(func.getId());
        assertFalse(deleted.isPresent());
    }
}