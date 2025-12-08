package ru.ssau.tk.NAME.PROJECT.datafordatabase;

import ru.ssau.tk.NAME.PROJECT.repository.UserRepository;
import ru.ssau.tk.NAME.PROJECT.repository.FunctionRepository;
import ru.ssau.tk.NAME.PROJECT.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FunctionRepository functionRepository;
    private final PointRepository pointRepository;
    private final DataGenerator dataGenerator;

    private static final int USER_COUNT = 50;           // Уменьшено для тестирования
    private static final int FUNCTIONS_PER_USER = 2;    // По 2 функции на пользователя
    private static final int MIN_POINTS_PER_FUNCTION = 10; // Минимум точек на функцию
    private static final int MAX_POINTS_PER_FUNCTION = 30; // Максимум точек на функцию

    private static final boolean CLEAR_DATABASE = true;     // Очищать базу перед заполнением
    private static final boolean GENERATE_DATA = true;      // Генерировать данные
    private static final boolean GENERATE_SORTING_DATA = false; // Генерация спецданных для сортировки
    private static final boolean PRINT_DETAILED_STATS = true;   // Подробная статистика

    @Override
    public void run(String... args) throws Exception {
        log.info("=== НАЧАЛО ЗАПОЛНЕНИЯ БАЗЫ ДАННЫХ ТЕСТОВЫМИ ДАННЫМИ ===");

        // Проверка аргументов командной строки
        if (args.length > 0) {
            processCommandLineArgs(args);
        }

        // Очистка существующих данных
        if (CLEAR_DATABASE) {
            clearDatabase();
        }

        // Генерация тестовых данных
        if (GENERATE_DATA) {
            if (GENERATE_SORTING_DATA) {
                generateAndSaveSortingData();
            } else {
                generateAndSaveTestData();
            }
        }

        log.info("=== ЗАПОЛНЕНИЕ БАЗЫ ДАННЫХ ЗАВЕРШЕНО ===");
    }


    private void generateAndSaveTestData() {
        log.info("Генерация тестовых данных...");
        log.info("Конфигурация: {} пользователей, {} функций на пользователя, {} точек на функцию",
                USER_COUNT, FUNCTIONS_PER_USER,
                String.format("%d-%d", MIN_POINTS_PER_FUNCTION, MAX_POINTS_PER_FUNCTION));

        // Генерация данных
        DataGenerator.TestData testData = dataGenerator.generateTestData(
                USER_COUNT,
                FUNCTIONS_PER_USER,
                MIN_POINTS_PER_FUNCTION,
                MAX_POINTS_PER_FUNCTION
        );

        // Сохранение пользователей
        log.info("Сохранение {} пользователей...", testData.users.size());
        userRepository.saveAll(testData.users);

        // Сохранение функций
        log.info("Сохранение {} функций...", testData.functions.size());
        functionRepository.saveAll(testData.functions);

        // Сохранение точек
        log.info("Сохранение {} точек...", testData.points.size());
        pointRepository.saveAll(testData.points);

        // Вывод статистики
        printStatistics(testData);
    }


    private void generateAndSaveSortingData() {
        log.info("Генерация данных для тестирования сортировок...");

        // Генерация специальных данных
        DataGenerator.TestData testData = dataGenerator.generateDataForSortingTests();

        // Сохранение пользователей
        log.info("Сохранение {} пользователей...", testData.users.size());
        userRepository.saveAll(testData.users);

        // Сохранение функций
        log.info("Сохранение {} функций...", testData.functions.size());
        functionRepository.saveAll(testData.functions);

        // Сохранение точек
        log.info("Сохранение {} точек...", testData.points.size());
        pointRepository.saveAll(testData.points);

        // Вывод детальной статистики
        printDetailedStatistics(testData);
    }


    private void clearDatabase() {
        log.info("Очистка базы данных перед заполнением...");

        try {
            // Удаление точек (должно быть первым из-за foreign key constraints)
            pointRepository.deleteAll();
            log.info("  - Точки: удалены");

            // Удаление функций
            functionRepository.deleteAll();
            log.info("  - Функции: удалены");

            // Удаление пользователей
            userRepository.deleteAll();
            log.info("  - Пользователи: удалены");

            log.info("База данных очищена успешно");
        } catch (Exception e) {
            log.error("Ошибка при очистке базы данных: {}", e.getMessage());
        }
    }

    private void printStatistics(DataGenerator.TestData testData) {
        log.info("=== СТАТИСТИКА ЗАПОЛНЕННЫХ ДАННЫХ ===");
        log.info("Общее количество записей:");
        log.info("  - Пользователи: {}", testData.users.size());
        log.info("  - Функции: {}", testData.functions.size());
        log.info("  - Точки: {}", testData.points.size());
        log.info("  - Всего записей: {}",
                testData.users.size() + testData.functions.size() + testData.points.size());

        if (!testData.users.isEmpty()) {
            long adminCount = testData.users.stream()
                    .filter(user -> user.getRole() == ru.ssau.tk.NAME.PROJECT.entity.User.Role.ADMIN)
                    .count();
            long userCount = testData.users.stream()
                    .filter(user -> user.getRole() == ru.ssau.tk.NAME.PROJECT.entity.User.Role.USER)
                    .count();
            long moderatorCount = testData.users.stream()
                    .filter(user -> user.getRole() == ru.ssau.tk.NAME.PROJECT.entity.User.Role.MODERATOR)
                    .count();
            long guestCount = testData.users.stream()
                    .filter(user -> user.getRole() == ru.ssau.tk.NAME.PROJECT.entity.User.Role.GUEST)
                    .count();

            log.info("Распределение пользователей по ролям:");
            log.info("  - ADMIN: {} ({}%)", adminCount,
                    String.format("%.1f", (adminCount * 100.0) / testData.users.size()));
            log.info("  - USER: {} ({}%)", userCount,
                    String.format("%.1f", (userCount * 100.0) / testData.users.size()));
            log.info("  - MODERATOR: {} ({}%)", moderatorCount,
                    String.format("%.1f", (moderatorCount * 100.0) / testData.users.size()));
            log.info("  - GUEST: {} ({}%)", guestCount,
                    String.format("%.1f", (guestCount * 100.0) / testData.users.size()));
        }

        if (!testData.functions.isEmpty()) {
            log.info("Распределение функций по типам:");
            Map<String, Long> typeDistribution = testData.functions.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            ru.ssau.tk.NAME.PROJECT.entity.Function::getType,
                            java.util.stream.Collectors.counting()
                    ));

            typeDistribution.forEach((type, count) -> {
                log.info("  - {}: {} ({}%)", type, count,
                        String.format("%.1f", (count * 100.0) / testData.functions.size()));
            });
        }

        if (!testData.points.isEmpty()) {
            log.info("Статистика по точкам:");
            double avgPointsPerFunction = (double) testData.points.size() / testData.functions.size();
            log.info("  - Среднее количество точек на функцию: {:.1f}", avgPointsPerFunction);

            double minX = testData.points.stream()
                    .mapToDouble(p -> p.getXVal().doubleValue())
                    .min().orElse(0);
            double maxX = testData.points.stream()
                    .mapToDouble(p -> p.getXVal().doubleValue())
                    .max().orElse(0);
            double minY = testData.points.stream()
                    .mapToDouble(p -> p.getYVal().doubleValue())
                    .min().orElse(0);
            double maxY = testData.points.stream()
                    .mapToDouble(p -> p.getYVal().doubleValue())
                    .max().orElse(0);

            log.info("  - Диапазон значений X: [{:.2f}, {:.2f}]", minX, maxX);
            log.info("  - Диапазон значений Y: [{:.2f}, {:.2f}]", minY, maxY);
        }

        if (PRINT_DETAILED_STATS) {
            printDetailedStatistics(testData);
        }

        log.info("=====================================");
    }

    private void printDetailedStatistics(DataGenerator.TestData testData) {
        log.info("=== ДЕТАЛЬНАЯ СТАТИСТИКА ===");

        if (!testData.users.isEmpty()) {
            log.info("Примеры пользователей:");
            testData.users.stream().limit(3).forEach(user ->
                    log.info("  - {} (ID: {}, Роль: {})",
                            user.getName(), user.getId(), user.getRole())
            );
        }

        if (!testData.functions.isEmpty()) {
            log.info("Примеры функций:");
            testData.functions.stream().limit(3).forEach(func ->
                    log.info("  - {} (Тип: {}, Выражение: {}, Владелец: {})",
                            func.getName(), func.getType(), func.getExpression(),
                            func.getOwner() != null ? func.getOwner().getName() : "null")
            );
        }

        if (!testData.points.isEmpty()) {
            log.info("Примеры точек:");
            testData.points.stream().limit(3).forEach(point ->
                    log.info("  - (x={}, y={}) для функции: {}",
                            point.getXVal(), point.getYVal(),
                            point.getFunction() != null ? point.getFunction().getName() : "null")
            );
        }

        if (!testData.functions.isEmpty() && !testData.points.isEmpty()) {
            log.info("Распределение точек по функциям:");
            testData.functions.forEach(func -> {
                long pointCount = testData.points.stream()
                        .filter(p -> p.getFunction() != null &&
                                p.getFunction().getId() != null &&
                                p.getFunction().getId().equals(func.getId()))
                        .count();
                log.info("  - {}: {} точек", func.getName(), pointCount);
            });
        }
    }

    private void processCommandLineArgs(String[] args) {
        log.info("Обработка аргументов командной строки: {}", String.join(" ", args));

        for (String arg : args) {
            if (arg.startsWith("--users=")) {
                try {
                    int users = Integer.parseInt(arg.substring("--users=".length()));
                    log.info("Установлено количество пользователей: {}", users);
                } catch (NumberFormatException e) {
                    log.error("Неверный формат количества пользователей: {}", arg);
                }
            } else if (arg.equals("--clear")) {
                log.info("Режим очистки базы данных активирован");
            } else if (arg.equals("--no-generate")) {
                log.info("Генерация данных отключена");
            } else if (arg.equals("--sorting")) {
                log.info("Режим генерации данных для сортировки активирован");
            }
        }
    }
}