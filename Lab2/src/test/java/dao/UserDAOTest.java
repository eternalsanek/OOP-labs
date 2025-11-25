package dao;

import modelDB.User;
import org.junit.jupiter.api.*;
import testutils.TestDataFactory;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private static Connection conn;
    private static UserDAO userDAO;

    @BeforeAll
    static void setupAll() {
        conn = ConnectionFactory.getConnection();
        userDAO = new UserDAOImpl(conn);
    }

    @BeforeEach
    void cleanDatabase() throws Exception {
        conn.createStatement().execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }

    @Test
    void testCreateReadUpdateDeleteUser() {
        String name = TestDataFactory.randomUserName();
        String password = TestDataFactory.randomPassword();
        String role = TestDataFactory.randomRole();

        // CREATE
        userDAO.createUser(name, password, role);

        // READ
        Optional<User> loaded = userDAO.getUserByName(name);
        assertTrue(loaded.isPresent());
        assertEquals(role, loaded.get().getRole());

        // UPDATE
        String newName = TestDataFactory.randomUserName();
        String newPassword = TestDataFactory.randomPassword();
        String newRole = TestDataFactory.randomRole();
        userDAO.updateUser(loaded.get().getId(), newName, newPassword, newRole);

        Optional<User> updated = userDAO.getUserById(loaded.get().getId());
        assertTrue(updated.isPresent());
        assertEquals(newName, updated.get().getName());
        assertEquals(newRole, updated.get().getRole());

        // DELETE
        userDAO.deleteUser(updated.get().getId());
        Optional<User> deleted = userDAO.getUserById(updated.get().getId());
        assertFalse(deleted.isPresent());
    }
}
