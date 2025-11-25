// test/SearchServiceTest.java
package search;

import search.SearchService;
import service.DTOTransformService;
import dao.*;
import modelDB.User;
import modelDB.Function;
import modelDB.Point;
import org.junit.jupiter.api.*;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class SearchServiceTest {
    private static Connection connection;
    private static UserDAO userDAO;
    private static FunctionDAO functionDAO;
    private static PointDAO pointDAO;
    private static DTOTransformService dtoService;
    private static SearchService searchService;

    private User testUser1;
    private User testUser2;
    private Function testFunction1;
    private Function testFunction2;

    @BeforeAll
    static void setUpAll() {
        log.info("Начало тестирования системы поиска");
        try {
            // Инициализация соединения и DAO
            connection = ConnectionFactory.getConnection();
            userDAO = new UserDAOImpl(connection);
            functionDAO = new FunctionDAOImpl(connection);
            pointDAO = new PointDAOImpl(connection);

            // Инициализация DTO сервиса
            dtoService = new DTOTransformService(userDAO, functionDAO, pointDAO);

            // Инициализация сервиса поиска
            searchService = new SearchService(dtoService);

            log.debug("Все сервисы инициализированы для тестирования поиска");
        } catch (Exception e) {
            log.error("Ошибка инициализации тестов поиска: {}", e.getMessage());
            fail("Не удалось инициализировать тесты поиска");
        }
    }

    @BeforeEach
    void setUp() {
        log.debug("Подготовка тестовых данных для поиска");

        // Создание тестовых пользователей
        testUser1 = dtoService.createUser("alice_search", "hash1", "USER");
        testUser2 = dtoService.createUser("bob_search", "hash2", "ADMIN");

        // Создание тестовых функций
        testFunction1 = dtoService.createFunction(
                testUser1.getId(),
                "quadratic_function",
                "MathFunction",
                "x^2"
        );
        testFunction2 = dtoService.createFunction(
                testUser2.getId(),
                "linear_function",
                "TabulatedFunction",
                "2*x"
        );

        // Создание тестовых точек
        dtoService.createPoint(testFunction1.getId(), 1.0, 1.0);
        dtoService.createPoint(testFunction1.getId(), 2.0, 4.0);
        dtoService.createPoint(testFunction2.getId(), 1.0, 2.0);
        dtoService.createPoint(testFunction2.getId(), 3.0, 6.0);

        log.debug("Созданы тестовые данные: 2 пользователя, 2 функции, 4 точки");
    }

    @AfterEach
    void tearDown() {
        log.debug("Очистка тестовых данных поиска");
        try {
            // Удаляем все созданные точки
            List<Point> points1 = dtoService.getPointsByFunction(testFunction1.getId());
            for (Point point : points1) {
                dtoService.deletePoint(point.getId());
            }

            List<Point> points2 = dtoService.getPointsByFunction(testFunction2.getId());
            for (Point point : points2) {
                dtoService.deletePoint(point.getId());
            }

            // Удаляем функции
            if (testFunction1 != null) {
                dtoService.deleteFunction(testFunction1.getId());
            }
            if (testFunction2 != null) {
                dtoService.deleteFunction(testFunction2.getId());
            }

            // Удаляем пользователей
            if (testUser1 != null) {
                dtoService.deleteUser(testUser1.getId());
            }
            if (testUser2 != null) {
                dtoService.deleteUser(testUser2.getId());
            }

        } catch (Exception e) {
            log.warn("Ошибка при очистке тестовых данных поиска: {}", e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAll() {
        log.info("Завершение тестирования системы поиска");
        if (dtoService != null) {
            dtoService.close();
        }
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            log.warn("Ошибка при закрытии соединения: {}", e.getMessage());
        }
    }

    @Test
    void testSingleSearch() {
        log.info("=== Тестирование одиночного поиска ===");

        // Поиск пользователя по имени
        Optional<User> user = searchService.findOne(User.class, "name", "alice", "linear");
        assertTrue(user.isPresent(), "Пользователь Alice должен быть найден");
        assertEquals("alice_search", user.get().getName());
        assertEquals("USER", user.get().getRole());

        // Поиск функции по типу - исправлено: ищем по полному значению
        Optional<Function> function = searchService.findOne(Function.class, "type", "MathFunction", "linear");
        assertTrue(function.isPresent(), "Математическая функция должна быть найдена");
        assertEquals("MathFunction", function.get().getType());
        assertEquals("quadratic_function", function.get().getName());

        log.info("Одиночный поиск завершен успешно");
    }

    @Test
    void testMultipleSearch() {
        log.info("=== Тестирование множественного поиска ===");

        // Поиск всех пользователей с ролью USER
        List<User> users = searchService.findAll(User.class, "role", "USER", "linear");
        assertFalse(users.isEmpty(), "Должны быть найдены пользователи с ролью USER");
        assertTrue(users.stream().anyMatch(u -> u.getName().equals("alice_search")));

        // Поиск всех функций, содержащих "function" в названии
        List<Function> functions = searchService.findAll(Function.class, "name", "function", "linear");
        assertEquals(2, functions.size(), "Должны быть найдены обе функции");
        assertTrue(functions.stream().anyMatch(f -> f.getName().equals("quadratic_function")));
        assertTrue(functions.stream().anyMatch(f -> f.getName().equals("linear_function")));

        log.info("Множественный поиск завершен: найдено {} пользователей и {} функций",
                users.size(), functions.size());
    }

    @Test
    void testSearchWithSorting() {
        log.info("=== Тестирование поиска с сортировкой ===");

        // Создаем дополнительных пользователей для теста сортировки
        User user3 = dtoService.createUser("charlie_search", "hash3", "USER");
        User user4 = dtoService.createUser("david_search", "hash4", "USER");

        try {
            // Поиск с сортировкой по имени по возрастанию
            List<User> usersAsc = searchService.findAllSorted(
                    User.class, "role", "USER", "linear", "name", true
            );

            assertTrue(usersAsc.size() >= 3, "Должны быть найдены как минимум 3 пользователя");

            // Проверяем сортировку по имени (ASC)
            for (int i = 0; i < usersAsc.size() - 1; i++) {
                assertTrue(usersAsc.get(i).getName().compareTo(usersAsc.get(i + 1).getName()) <= 0,
                        "Пользователи должны быть отсортированы по имени по возрастанию");
            }

            // Поиск с сортировкой по имени по убыванию
            List<User> usersDesc = searchService.findAllSorted(
                    User.class, "role", "USER", "linear", "name", false
            );

            // Проверяем сортировку по имени (DESC)
            for (int i = 0; i < usersDesc.size() - 1; i++) {
                assertTrue(usersDesc.get(i).getName().compareTo(usersDesc.get(i + 1).getName()) >= 0,
                        "Пользователи должны быть отсортированы по имени по убыванию");
            }

        } finally {
            // Очистка дополнительных пользователей
            if (user3 != null) dtoService.deleteUser(user3.getId());
            if (user4 != null) dtoService.deleteUser(user4.getId());
        }

        log.info("Поиск с сортировкой завершен успешно");
    }

    @Test
    void testDepthFirstSearch() {
        log.info("=== Тестирование поиска в глубину ===");

        // DFS ищет по всей иерархии (User → Function → Point)
        List<Object> results = searchService.findAll(Object.class, "name", "alice", "dfs");
        assertFalse(results.isEmpty(), "DFS должен найти объекты связанные с Alice");

        // Должны найти пользователя Alice
        assertTrue(results.stream()
                        .anyMatch(r -> r instanceof User && ((User) r).getName().equals("alice_search")),
                "Должен быть найден пользователь Alice");

        log.info("DFS завершен: найдено {} объектов в иерархии", results.size());
    }

    @Test
    void testBreadthFirstSearch() {
        log.info("=== Тестирование поиска в ширину ===");

        // BFS ищет по уровням иерархии
        List<Object> results = searchService.findAll(Object.class, "type", "Math", "bfs");
        assertFalse(results.isEmpty(), "BFS должен найти объекты с типом Math");

        // Должны найти математическую функцию
        assertTrue(results.stream()
                        .anyMatch(r -> r instanceof Function &&
                                ((Function) r).getType().equals("MathFunction")),
                "Должна быть найдена математическая функция");

        log.info("BFS завершен: найдено {} объектов в иерархии", results.size());
    }

    @Test
    void testDifferentAlgorithms() {
        log.info("=== Тестирование разных алгоритмов ===");

        // Сравниваем линейный и бинарный поиск
        List<User> linearResults = searchService.findAll(User.class, "name", "search", "linear");
        List<User> binaryResults = searchService.findAll(User.class, "name", "search", "binary");

        assertEquals(linearResults.size(), binaryResults.size(),
                "Линейный и бинарный поиск должны возвращать одинаковое количество результатов");

        log.info("Сравнение алгоритмов: Линейный={}, Бинарный={}",
                linearResults.size(), binaryResults.size());
    }

    @Test
    void testPointSearch() {
        log.info("=== Тестирование поиска точек ===");

        // Поиск точек по координате X
        List<Point> points = searchService.findAll(Point.class, "xVal", "1.0", "linear");
        assertEquals(2, points.size(), "Должны быть найдены 2 точки с x=1.0");

        // Проверяем, что найдены точки с правильными координатами
        assertTrue(points.stream().anyMatch(p -> p.getYVal() == 1.0));
        assertTrue(points.stream().anyMatch(p -> p.getYVal() == 2.0));

        log.info("Поиск точек завершен: найдено {} точек с x=1.0", points.size());
    }

    @Test
    void testAvailableAlgorithms() {
        log.info("=== Тестирование получения доступных алгоритмов ===");

        List<String> algorithms = searchService.getAvailableAlgorithms();
        assertFalse(algorithms.isEmpty(), "Должен быть доступен хотя бы один алгоритм");

        // Проверяем основные алгоритмы
        assertTrue(algorithms.contains("Linear Search"), "Должен быть доступен линейный поиск");
        assertTrue(algorithms.contains("Binary Search"), "Должен быть доступен бинарный поиск");
        assertTrue(algorithms.contains("Depth First Search"), "Должен быть доступен DFS");
        assertTrue(algorithms.contains("Breadth First Search"), "Должен быть доступен BFS");

        log.info("Доступные алгоритмы поиска: {}", algorithms);
    }

    @Test
    void testSearchWithLimit() {
        log.info("=== Тестирование поиска с лимитом ===");

        // Создаем больше пользователей для теста лимита
        for (int i = 0; i < 5; i++) {
            dtoService.createUser("user_" + i, "hash_" + i, "USER");
        }

        try {
            // Поиск с лимитом 3 результата
            List<User> limitedResults = searchService.findAll(
                    User.class, "role", "USER", "linear", 3
            );

            assertEquals(3, limitedResults.size(), "Должны быть возвращены только 3 результата");

        } finally {
            // Очистка дополнительных пользователей
            List<User> allUsers = dtoService.getAllUsers();
            for (User user : allUsers) {
                if (user.getName().startsWith("user_")) {
                    dtoService.deleteUser(user.getId());
                }
            }
        }

        log.info("Поиск с лимитом завершен успешно");
    }
}