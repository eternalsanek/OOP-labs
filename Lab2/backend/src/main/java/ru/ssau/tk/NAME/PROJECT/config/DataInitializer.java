package ru.ssau.tk.NAME.PROJECT.config;

import ru.ssau.tk.NAME.PROJECT.entity.User;
import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import ru.ssau.tk.NAME.PROJECT.repository.UserRepository;
import ru.ssau.tk.NAME.PROJECT.repository.FunctionRepository;
import ru.ssau.tk.NAME.PROJECT.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final PointRepository pointRepository;

    private static final String[] USER_NAMES = {"Adam", "Barbara", "Charlie", "Diana", "Edward", "Fiona", "George", "Hannah", "Ivan", "Julia", "Kevin", "Laura", "Michael", "Nina", "Oliver", "Paula"};
    private static final String[] FUNCTION_TYPES = {"linear", "quadratic", "cubic", "exponential", "logarithmic", "trigonometric"};
    private static final String[] EXPRESSIONS = {"x + 1", "x^2", "x^3", "e^x", "log(x)", "sin(x)", "cos(x)", "tan(x)"};

    private final Random random = new Random();

    // Конфигурация генерации данных
    private static final int USER_COUNT = 50;
    private static final int FUNCTIONS_PER_USER = 2;
    private static final int MIN_POINTS_PER_FUNCTION = 10;
    private static final int MAX_POINTS_PER_FUNCTION = 30;
    private static final boolean CLEAR_DATABASE = true;

    @Bean
    @Order(1)
    public CommandLineRunner initSecurityDefaults() {
        return args -> {
            log.info("=== ИНИЦИАЛИЗАЦИЯ НАСТРОЕК БЕЗОПАСНОСТИ ===");

            // Проверка наличия PasswordEncoder
            if (passwordEncoder == null) {
                log.error("PasswordEncoder не инициализирован! Проверьте конфигурацию Spring Security.");
                throw new IllegalStateException("PasswordEncoder не инициализирован");
            }

            // Тестовое кодирование пароля для проверки работы encoder
            String testPassword = "test123";
            String encoded = passwordEncoder.encode(testPassword);
            log.info("PasswordEncoder работает корректно. Тестовый пароль закодирован: {}",
                    passwordEncoder.matches(testPassword, encoded) ? "ДА" : "НЕТ");

            log.info("Инициализация безопасности завершена");
        };
    }

    @Bean
    @Order(2)
    @DependsOn("initSecurityDefaults")
    public CommandLineRunner initData() {
        return args -> {
            log.info("=== НАЧАЛО ИНИЦИАЛИЗАЦИИ БАЗЫ ДАННЫХ ===");

            // Очистка существующих данных
            if (CLEAR_DATABASE) {
                clearDatabase();
            }

            // Создание дефолтных пользователей с Spring Security
            createSecurityUsers();

            // Генерация тестовых данных
            generateTestData();

            log.info("=== ИНИЦИАЛИЗАЦИЯ БАЗЫ ДАННЫХ ЗАВЕРШЕНА ===");
        };
    }

    private void createSecurityUsers() {
        log.info("Создание пользователей Spring Security...");

        // Администратор с полными правами
        if (!userRepository.existsByName("admin")) {
            User admin = new User();
            admin.setName("admin");
            // Кодируем пароль через PasswordEncoder
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            log.info("✅ Создан администратор: admin / admin123 (пароль закодирован)");
        } else {
            log.info("⚠️ Администратор уже существует");
        }

        // Модератор с расширенными правами
        if (!userRepository.existsByName("moderator")) {
            User moderator = new User();
            moderator.setName("moderator");
            moderator.setPasswordHash(passwordEncoder.encode("mod123"));
            moderator.setRole(User.Role.MODERATOR);
            userRepository.save(moderator);
            log.info("✅ Создан модератор: moderator / mod123 (пароль закодирован)");
        } else {
            log.info("⚠️ Модератор уже существует");
        }

        // Обычный пользователь
        if (!userRepository.existsByName("user")) {
            User user = new User();
            user.setName("user");
            user.setPasswordHash(passwordEncoder.encode("user123"));
            user.setRole(User.Role.USER);
            userRepository.save(user);
            log.info("✅ Создан обычный пользователь: user / user123 (пароль закодирован)");
        } else {
            log.info("⚠️ Пользователь уже существует");
        }

        // Гость с ограниченными правами
        if (!userRepository.existsByName("guest")) {
            User guest = new User();
            guest.setName("guest");
            guest.setPasswordHash(passwordEncoder.encode("guest123"));
            guest.setRole(User.Role.GUEST);
            userRepository.save(guest);
            log.info("✅ Создан гость: guest / guest123 (пароль закодирован)");
        } else {
            log.info("⚠️ Гость уже существует");
        }

        // Проверяем созданных пользователей
        List<User> defaultUsers = userRepository.findAll();
        log.info("Всего пользователей в базе: {}", defaultUsers.size());

        // Логируем информацию о пользователях
        defaultUsers.forEach(user -> {
            String passwordCheck = passwordEncoder.matches("wrong_password", user.getPasswordHash()) ?
                    "НЕКОРРЕКТНО" : "корректно";
            log.debug("Пользователь: {}, Роль: {}, Пароль закодирован: {}",
                    user.getName(), user.getRole(), passwordCheck);
        });
    }

    private void generateTestData() {
        log.info("Генерация тестовых данных...");
        log.info("Конфигурация: {} пользователей, {} функций на пользователя, {} точек на функцию",
                USER_COUNT, FUNCTIONS_PER_USER,
                String.format("%d-%d", MIN_POINTS_PER_FUNCTION, MAX_POINTS_PER_FUNCTION));

        // Получаем всех пользователей (включая дефолтных)
        List<User> allUsers = userRepository.findAll();

        // Генерация дополнительных пользователей
        if (allUsers.size() < USER_COUNT) {
            int usersToGenerate = USER_COUNT - allUsers.size();
            log.info("Генерация {} дополнительных пользователей", usersToGenerate);

            List<User> generatedUsers = generateUsers(usersToGenerate);
            userRepository.saveAll(generatedUsers);
            log.info("Сохранено {} сгенерированных пользователей", generatedUsers.size());

            // Обновляем список всех пользователей
            allUsers = userRepository.findAll();
        }

        // Генерация функций
        List<Function> functions = generateFunctions(allUsers, FUNCTIONS_PER_USER);
        functionRepository.saveAll(functions);
        log.info("Сохранено {} функций", functions.size());

        // Генерация точек
        List<Point> points = generatePoints(functions, MIN_POINTS_PER_FUNCTION, MAX_POINTS_PER_FUNCTION);
        pointRepository.saveAll(points);
        log.info("Сохранено {} точек", points.size());

        // Вывод статистики
        printStatistics(allUsers, functions, points);
    }

    private void clearDatabase() {
        log.info("Очистка базы данных перед заполнением...");

        try {
            // Удаляем в правильном порядке из-за foreign key constraints
            long pointsCount = pointRepository.count();
            pointRepository.deleteAll();
            log.info("  - Точки: удалено {} записей", pointsCount);

            long functionsCount = functionRepository.count();
            functionRepository.deleteAll();
            log.info("  - Функции: удалено {} записей", functionsCount);

            long usersCount = userRepository.count();
            userRepository.deleteAll();
            log.info("  - Пользователи: удалено {} записей", usersCount);

            log.info("База данных очищена успешно");
        } catch (Exception e) {
            log.error("Ошибка при очистке базы данных: {}", e.getMessage());
            log.error("Стек вызовов:", e);
        }
    }

    private List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();
        User.Role[] roles = User.Role.values();

        for (int i = 0; i < count; i++) {
            String name = USER_NAMES[random.nextInt(USER_NAMES.length)] + "_" + String.format("%04d", random.nextInt(10000));
            User.Role role = roles[random.nextInt(roles.length)];

            User user = new User();
            user.setName(name);
            // Всегда используем PasswordEncoder для кодирования паролей
            user.setPasswordHash(passwordEncoder.encode("password" + i + "_" + System.currentTimeMillis()));
            user.setRole(role);
            users.add(user);
        }
        return users;
    }

    private List<Function> generateFunctions(List<User> users, int functionsPerUser) {
        List<Function> functions = new ArrayList<>();

        for (User user : users) {
            for (int j = 0; j < functionsPerUser; j++) {
                String type = FUNCTION_TYPES[random.nextInt(FUNCTION_TYPES.length)];
                String expression = EXPRESSIONS[random.nextInt(EXPRESSIONS.length)];
                String name = "Func_" + type + "_" + String.format("%03d", random.nextInt(1000)) + "_" + user.getName();

                Function function = new Function();
                function.setOwner(user);
                function.setName(name);
                function.setType(type);
                function.setExpression(expression);
                functions.add(function);
            }
        }
        return functions;
    }

    private List<Point> generatePoints(List<Function> functions, int minPointsPerFunction, int maxPointsPerFunction) {
        List<Point> points = new ArrayList<>();

        for (Function function : functions) {
            int pointsCount = minPointsPerFunction + random.nextInt(maxPointsPerFunction - minPointsPerFunction + 1);

            switch (function.getType().toLowerCase()) {
                case "linear":
                    generateLinearPoints(function, pointsCount, points);
                    break;
                case "quadratic":
                    generateQuadraticPoints(function, pointsCount, points);
                    break;
                case "cubic":
                    generateCubicPoints(function, pointsCount, points);
                    break;
                case "exponential":
                    generateExponentialPoints(function, pointsCount, points);
                    break;
                case "logarithmic":
                    generateLogarithmicPoints(function, pointsCount, points);
                    break;
                case "trigonometric":
                    generateTrigonometricPoints(function, pointsCount, points);
                    break;
                default:
                    generateRandomPoints(function, pointsCount, points);
                    break;
            }
        }
        return points;
    }

    private void generateLinearPoints(Function function, int count, List<Point> points) {
        double slope = random.nextDouble() * 4 - 2;
        double intercept = random.nextDouble() * 10 - 5;

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 20 - 10;
            double y = slope * x + intercept;
            points.add(createPoint(function, x, y));
        }
    }

    private void generateQuadraticPoints(Function function, int count, List<Point> points) {
        double a = random.nextDouble() * 2 - 1;
        double b = random.nextDouble() * 4 - 2;
        double c = random.nextDouble() * 10 - 5;

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 10 - 5;
            double y = a * x * x + b * x + c;
            points.add(createPoint(function, x, y));
        }
    }

    private void generateCubicPoints(Function function, int count, List<Point> points) {
        double a = random.nextDouble() * 0.5 - 0.25;
        double b = random.nextDouble() * 2 - 1;
        double c = random.nextDouble() * 4 - 2;
        double d = random.nextDouble() * 10 - 5;

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 8 - 4;
            double y = a * x * x * x + b * x * x + c * x + d;
            points.add(createPoint(function, x, y));
        }
    }

    private void generateExponentialPoints(Function function, int count, List<Point> points) {
        double base = random.nextDouble() + 0.5;
        double coefficient = random.nextDouble() * 3;

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 5;
            double y = coefficient * Math.pow(base, x);
            points.add(createPoint(function, x, y));
        }
    }

    private void generateLogarithmicPoints(Function function, int count, List<Point> points) {
        double coefficient = random.nextDouble() * 2 + 0.5;

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 9 + 1;
            double y = coefficient * Math.log(x);
            points.add(createPoint(function, x, y));
        }
    }

    private void generateTrigonometricPoints(Function function, int count, List<Point> points) {
        String expression = function.getExpression().toLowerCase();
        double amplitude = random.nextDouble() * 3 + 0.5;
        double frequency = random.nextDouble() * 2 + 0.5;
        double phase = random.nextDouble() * Math.PI * 2;

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 4 * Math.PI;

            double y;
            if (expression.contains("sin")) {
                y = amplitude * Math.sin(frequency * x + phase);
            } else if (expression.contains("cos")) {
                y = amplitude * Math.cos(frequency * x + phase);
            } else if (expression.contains("tan")) {
                y = amplitude * Math.tan(frequency * x + phase);
                if (Math.abs(y) > 100) {
                    y = 100 * Math.signum(y);
                }
            } else {
                y = amplitude * Math.sin(frequency * x + phase);
            }

            points.add(createPoint(function, x, y));
        }
    }

    private void generateRandomPoints(Function function, int count, List<Point> points) {
        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 20 - 10;
            double y = random.nextDouble() * 20 - 10;
            points.add(createPoint(function, x, y));
        }
    }

    private Point createPoint(Function function, double x, double y) {
        BigDecimal xVal = BigDecimal.valueOf(x).setScale(4, RoundingMode.HALF_UP);
        BigDecimal yVal = BigDecimal.valueOf(y).setScale(4, RoundingMode.HALF_UP);

        Point point = new Point();
        point.setFunction(function);
        point.setXVal(xVal);
        point.setYVal(yVal);

        return point;
    }

    private void printStatistics(List<User> users, List<Function> functions, List<Point> points) {
        log.info("=== СТАТИСТИКА ЗАПОЛНЕННЫХ ДАННЫХ ===");
        log.info("Общее количество записей:");
        log.info("  - Пользователи: {}", users.size());
        log.info("  - Функции: {}", functions.size());
        log.info("  - Точки: {}", points.size());
        log.info("  - Всего записей: {}", users.size() + functions.size() + points.size());

        if (!users.isEmpty()) {
            long adminCount = users.stream()
                    .filter(user -> user.getRole() == User.Role.ADMIN)
                    .count();
            long userCount = users.stream()
                    .filter(user -> user.getRole() == User.Role.USER)
                    .count();
            long moderatorCount = users.stream()
                    .filter(user -> user.getRole() == User.Role.MODERATOR)
                    .count();
            long guestCount = users.stream()
                    .filter(user -> user.getRole() == User.Role.GUEST)
                    .count();

            log.info("Распределение пользователей по ролям:");
            log.info("  - ADMIN: {} ({}%)", adminCount,
                    String.format("%.1f", (adminCount * 100.0) / users.size()));
            log.info("  - USER: {} ({}%)", userCount,
                    String.format("%.1f", (userCount * 100.0) / users.size()));
            log.info("  - MODERATOR: {} ({}%)", moderatorCount,
                    String.format("%.1f", (moderatorCount * 100.0) / users.size()));
            log.info("  - GUEST: {} ({}%)", guestCount,
                    String.format("%.1f", (guestCount * 100.0) / users.size()));
        }

        if (!functions.isEmpty()) {
            log.info("Распределение функций по типам:");
            java.util.Map<String, Long> typeDistribution = functions.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            Function::getType,
                            java.util.stream.Collectors.counting()
                    ));

            typeDistribution.forEach((type, count) -> {
                log.info("  - {}: {} ({}%)", type, count,
                        String.format("%.1f", (count * 100.0) / functions.size()));
            });
        }

        if (!points.isEmpty()) {
            log.info("Статистика по точкам:");
            double avgPointsPerFunction = (double) points.size() / functions.size();
            log.info("  - Среднее количество точек на функцию: {:.1f}", avgPointsPerFunction);

            double minX = points.stream()
                    .mapToDouble(p -> p.getXVal().doubleValue())
                    .min().orElse(0);
            double maxX = points.stream()
                    .mapToDouble(p -> p.getXVal().doubleValue())
                    .max().orElse(0);
            double minY = points.stream()
                    .mapToDouble(p -> p.getYVal().doubleValue())
                    .min().orElse(0);
            double maxY = points.stream()
                    .mapToDouble(p -> p.getYVal().doubleValue())
                    .max().orElse(0);

            log.info("  - Диапазон значений X: [{:.2f}, {:.2f}]", minX, maxX);
            log.info("  - Диапазон значений Y: [{:.2f}, {:.2f}]", minY, maxY);
        }

        log.info("=====================================");
    }
}
