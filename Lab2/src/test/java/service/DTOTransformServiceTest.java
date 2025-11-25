// test/DTOServiceTest.java
package service;

import modelDB.User;
import modelDB.Function;
import modelDB.Point;
import service.DTOTransformService;
import dao.*;
import org.junit.jupiter.api.*;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DTOTransformServiceTest {
    private static Connection connection;
    private static UserDAO userDAO;
    private static FunctionDAO functionDAO;
    private static PointDAO pointDAO;
    private static DTOTransformService dtoService;

    private User testUser;
    private Function testFunction;
    private Point testPoint;

    @BeforeAll
    static void setUpAll() {
        log.info("Начало тестирования DTO сервиса");
        try {
            connection = ConnectionFactory.getConnection();
            userDAO = new UserDAOImpl(connection);
            functionDAO = new FunctionDAOImpl(connection);
            pointDAO = new PointDAOImpl(connection);
            dtoService = new DTOTransformService(userDAO, functionDAO, pointDAO);
        } catch (Exception e) {
            log.error("Ошибка инициализации тестов: {}", e.getMessage());
            fail("Не удалось инициализировать тесты");
        }
    }

    @BeforeEach
    void setUp() {
        log.debug("Подготовка тестовых данных");
        // Создаем тестового пользователя
        String randomName = "testUser_" + UUID.randomUUID().toString().substring(0, 8);
        testUser = dtoService.createUser(randomName, "testHash", "USER");

        // Создаем тестовую функцию
        testFunction = dtoService.createFunction(
                testUser.getId(),
                "testFunction",
                "MathFunction",
                "x^2"
        );

        // Создаем тестовую точку
        testPoint = dtoService.createPoint(testFunction.getId(), 1.0, 1.0);
    }

    @AfterEach
    void tearDown() {
        log.debug("Очистка тестовых данных");
        try {
            // Удаляем тестовые данные в правильном порядке
            if (testPoint != null) {
                dtoService.deletePoint(testPoint.getId());
            }
            if (testFunction != null) {
                dtoService.deleteFunction(testFunction.getId());
            }
            if (testUser != null) {
                dtoService.deleteUser(testUser.getId());
            }
        } catch (Exception e) {
            log.warn("Ошибка при очистке тестовых данных: {}", e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
        log.info("Завершение тестирования DTO сервиса");
        if (dtoService != null) {
            dtoService.close();
        }
    }

    @Test
    void testUserCRUD() {
        log.info("Тестирование CRUD операций для пользователя");

        // Test Create & Read
        assertNotNull(testUser.getId());
        assertEquals("USER", testUser.getRole());

        Optional<User> foundUser = dtoService.getUserById(testUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getName(), foundUser.get().getName());

        // Test Update
        User updatedUser = dtoService.updateUser(
                testUser.getId(),
                "updatedName",
                "newHash",
                "ADMIN"
        );
        assertEquals("updatedName", updatedUser.getName());
        assertEquals("ADMIN", updatedUser.getRole());

        // Test Get All
        List<User> allUsers = dtoService.getAllUsers();
        assertFalse(allUsers.isEmpty());

        // Test Get By Name
        Optional<User> userByName = dtoService.getUserByName("updatedName");
        assertTrue(userByName.isPresent());
    }

    @Test
    void testFunctionCRUD() {
        log.info("Тестирование CRUD операций для функции");

        // Test Create & Read
        assertNotNull(testFunction.getId());
        assertEquals("MathFunction", testFunction.getType());
        assertEquals(testUser.getId(), testFunction.getOwnerId());

        Optional<Function> foundFunction = dtoService.getFunctionById(testFunction.getId());
        assertTrue(foundFunction.isPresent());
        assertEquals(testFunction.getName(), foundFunction.get().getName());

        // Test Update - ИСПРАВЛЕННАЯ СТРОКА
        Function updatedFunction = dtoService.updateFunction(  // ← исправлено с User на Function
                testFunction.getId(),
                "updatedFunction",
                "TabulatedFunction",
                "x^3"
        );
        assertEquals("updatedFunction", updatedFunction.getName());
        assertEquals("TabulatedFunction", updatedFunction.getType());
        assertEquals("x^3", updatedFunction.getExpression());

        // Test Get By Owner
        List<Function> userFunctions = dtoService.getFunctionsByOwner(testUser.getId());
        assertFalse(userFunctions.isEmpty());
        assertTrue(userFunctions.stream()
                .anyMatch(f -> f.getName().equals("updatedFunction")));
    }

    @Test
    void testPointCRUD() {
        log.info("Тестирование CRUD операций для точки");

        // Test Create & Read
        assertNotNull(testPoint.getId());
        assertEquals(1.0, testPoint.getXVal(), 0.001);
        assertEquals(1.0, testPoint.getYVal(), 0.001);

        Optional<Point> foundPoint = dtoService.getPointById(testPoint.getId());
        assertTrue(foundPoint.isPresent());
        assertEquals(testPoint.getFunctionId(), foundPoint.get().getFunctionId());

        // Test Update
        Point updatedPoint = dtoService.updatePoint(testPoint.getId(), 2.0, 4.0);
        assertEquals(2.0, updatedPoint.getXVal(), 0.001);
        assertEquals(4.0, updatedPoint.getYVal(), 0.001);

        // Test Get By Function
        List<Point> functionPoints = dtoService.getPointsByFunction(testFunction.getId());
        assertFalse(functionPoints.isEmpty());
        assertTrue(functionPoints.stream()
                .anyMatch(p -> p.getXVal() == 2.0 && p.getYVal() == 4.0));
    }

    @Test
    void testUserDeletionCascade() {
        log.info("Тестирование каскадного удаления пользователя");

        // Создаем дополнительные данные для теста каскадного удаления
        Function extraFunction = dtoService.createFunction(
                testUser.getId(),
                "extraFunction",
                "TabulatedFunction",
                "sin(x)"
        );

        Point extraPoint1 = dtoService.createPoint(extraFunction.getId(), 0.0, 0.0);
        Point extraPoint2 = dtoService.createPoint(extraFunction.getId(), 1.0, 1.0);

        // Удаляем пользователя
        dtoService.deleteUser(testUser.getId());

        // Проверяем, что все данные удалены
        Optional<User> deletedUser = dtoService.getUserById(testUser.getId());
        assertFalse(deletedUser.isPresent());

        List<Function> userFunctions = dtoService.getFunctionsByOwner(testUser.getId());
        assertTrue(userFunctions.isEmpty());

        // Очищаем ссылки чтобы tearDown не пытался удалить уже удаленные данные
        testUser = null;
        testFunction = null;
        testPoint = null;
    }

    @Test
    void testFunctionDeletionCascade() {
        log.info("Тестирование каскадного удаления функции");

        // Создаем дополнительные точки
        Point point1 = dtoService.createPoint(testFunction.getId(), 0.0, 0.0);
        Point point2 = dtoService.createPoint(testFunction.getId(), 2.0, 4.0);
        Point point3 = dtoService.createPoint(testFunction.getId(), 3.0, 9.0);

        // Удаляем функцию
        dtoService.deleteFunction(testFunction.getId());

        // Проверяем, что функция и все её точки удалены
        Optional<Function> deletedFunction = dtoService.getFunctionById(testFunction.getId());
        assertFalse(deletedFunction.isPresent());

        List<Point> functionPoints = dtoService.getPointsByFunction(testFunction.getId());
        assertTrue(functionPoints.isEmpty());

        // Очищаем ссылки
        testFunction = null;
        testPoint = null;
    }

    @Test
    void testValidation() {
        log.info("Тестирование валидации данных");

        // Test invalid role
        IllegalArgumentException roleException = assertThrows(
                IllegalArgumentException.class,
                () -> dtoService.createUser("invalidUser", "hash", "INVALID_ROLE")
        );
        assertFalse(roleException.getMessage().contains("роль должна быть"));

        IllegalArgumentException typeException = assertThrows(
                IllegalArgumentException.class,
                () -> dtoService.createFunction(
                        testUser.getId(),
                        "invalidFunction",
                        "InvalidType",
                        "expression"
                )
        );
        assertFalse(typeException.getMessage().contains("тип функции должен быть"));
    }

    @Test
    void testGetUserByName() {
        log.info("Тестирование поиска пользователя по имени");

        Optional<User> foundUser = dtoService.getUserByName(testUser.getName());
        assertTrue(foundUser.isPresent());
        assertEquals(testUser.getId(), foundUser.get().getId());

        Optional<User> nonExistentUser = dtoService.getUserByName("nonExistentUser");
        assertFalse(nonExistentUser.isPresent());
    }

    @Test
    void testGetPointById() {
        log.info("Тестирование поиска точки по ID");

        Optional<Point> foundPoint = dtoService.getPointById(testPoint.getId());
        assertTrue(foundPoint.isPresent());
        assertEquals(testPoint.getXVal(), foundPoint.get().getXVal(), 0.001);
        assertEquals(testPoint.getYVal(), foundPoint.get().getYVal(), 0.001);

        Optional<Point> nonExistentPoint = dtoService.getPointById(UUID.randomUUID());
        assertFalse(nonExistentPoint.isPresent());
    }

    @Test
    void testGetFunctionById() {
        log.info("Тестирование поиска функции по ID");

        Optional<Function> foundFunction = dtoService.getFunctionById(testFunction.getId());
        assertTrue(foundFunction.isPresent());
        assertEquals(testFunction.getName(), foundFunction.get().getName());
        assertEquals(testFunction.getType(), foundFunction.get().getType());

        Optional<Function> nonExistentFunction = dtoService.getFunctionById(UUID.randomUUID());
        assertFalse(nonExistentFunction.isPresent());
    }

    private boolean isSorted(List<Double> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i + 1)) {
                return false;
            }
        }
        return true;
    }
}