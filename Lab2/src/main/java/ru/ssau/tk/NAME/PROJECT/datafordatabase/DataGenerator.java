package ru.ssau.tk.NAME.PROJECT.datafordatabase;

import ru.ssau.tk.NAME.PROJECT.entity.User;
import ru.ssau.tk.NAME.PROJECT.entity.Function;
import ru.ssau.tk.NAME.PROJECT.entity.Point;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
public class DataGenerator {

    // Специальные данные для тестирования сортировок
    private static final String[] USER_NAMES = {"Adam", "Barbara", "Charlie", "Diana", "Edward", "Fiona", "George", "Hannah", "Ivan", "Julia", "Kevin", "Laura", "Michael", "Nina", "Oliver", "Paula"};

    private static final String[] FUNCTION_TYPES = {"linear", "quadratic", "cubic", "exponential", "logarithmic", "trigonometric"};

    private static final String[] EXPRESSIONS = {"x + 1", "x^2", "x^3", "e^x", "log(x)", "sin(x)", "cos(x)", "tan(x)"};

    private final Random random = new Random();

    public List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();
        User.Role[] roles = User.Role.values();
        for (int i = 0; i < count; i++) {
            String name = USER_NAMES[random.nextInt(USER_NAMES.length)] + "_" + String.format("%04d", random.nextInt(10000));
            User.Role role = roles[random.nextInt(roles.length)];
            User user = new User();
            user.setName(name);
            user.setPasswordHash("hash_" + i);
            user.setRole(role);
            users.add(user);
        }
        return users;
    }

    public List<Function> generateFunctions(List<User> users, int functionsPerUser) {
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

    public List<Point> generatePoints(List<Function> functions, int minPointsPerFunction, int maxPointsPerFunction) {
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
        double slope = random.nextDouble() * 4 - 2; // коэффициент от -2 до 2
        double intercept = random.nextDouble() * 10 - 5; // смещение от -5 до 5

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 20 - 10; // x от -10 до 10
            double y = slope * x + intercept;
            points.add(createPoint(function, x, y));
        }
    }

    private void generateQuadraticPoints(Function function, int count, List<Point> points) {
        double a = random.nextDouble() * 2 - 1; // от -1 до 1
        double b = random.nextDouble() * 4 - 2; // от -2 до 2
        double c = random.nextDouble() * 10 - 5; // от -5 до 5

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 10 - 5; // x от -5 до 5
            double y = a * x * x + b * x + c;
            points.add(createPoint(function, x, y));
        }
    }

    private void generateCubicPoints(Function function, int count, List<Point> points) {
        double a = random.nextDouble() * 0.5 - 0.25; // от -0.25 до 0.25
        double b = random.nextDouble() * 2 - 1; // от -1 до 1
        double c = random.nextDouble() * 4 - 2; // от -2 до 2
        double d = random.nextDouble() * 10 - 5; // от -5 до 5

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 8 - 4; // x от -4 до 4
            double y = a * x * x * x + b * x * x + c * x + d;
            points.add(createPoint(function, x, y));
        }
    }

    private void generateExponentialPoints(Function function, int count, List<Point> points) {
        double base = random.nextDouble() + 0.5; // от 0.5 до 1.5
        double coefficient = random.nextDouble() * 3; // от 0 до 3

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 5; // x от 0 до 5
            double y = coefficient * Math.pow(base, x);
            points.add(createPoint(function, x, y));
        }
    }

    private void generateLogarithmicPoints(Function function, int count, List<Point> points) {
        double coefficient = random.nextDouble() * 2 + 0.5; // от 0.5 до 2.5

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 9 + 1; // x от 1 до 10
            double y = coefficient * Math.log(x);
            points.add(createPoint(function, x, y));
        }
    }

    private void generateTrigonometricPoints(Function function, int count, List<Point> points) {
        String expression = function.getExpression().toLowerCase();
        double amplitude = random.nextDouble() * 3 + 0.5; // от 0.5 до 3.5
        double frequency = random.nextDouble() * 2 + 0.5; // от 0.5 до 2.5
        double phase = random.nextDouble() * Math.PI * 2; // от 0 до 2π

        for (int i = 0; i < count; i++) {
            double x = random.nextDouble() * 4 * Math.PI; // x от 0 до 4π

            double y;
            if (expression.contains("sin")) {
                y = amplitude * Math.sin(frequency * x + phase);
            } else if (expression.contains("cos")) {
                y = amplitude * Math.cos(frequency * x + phase);
            } else if (expression.contains("tan")) {
                y = amplitude * Math.tan(frequency * x + phase);
                // Избегаем бесконечных значений
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
            double x = random.nextDouble() * 20 - 10; // x от -10 до 10
            double y = random.nextDouble() * 20 - 10; // y от -10 до 10
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

        function.getPoints().add(point);

        return point;
    }

    public TestData generateTestData(int userCount, int functionsPerUser, int minPointsPerFunction, int maxPointsPerFunction) {
        List<User> users = generateUsers(userCount);
        List<Function> functions = generateFunctions(users, functionsPerUser);
        List<Point> points = generatePoints(functions, minPointsPerFunction, maxPointsPerFunction);

        log.info("Generated test data: {} users, {} functions, {} points",
                users.size(), functions.size(), points.size());

        return new TestData(users, functions, points);
    }

    public TestData generateTestData(int userCount, int functionsPerUser) {
        return generateTestData(userCount, functionsPerUser, 5, 15);
    }

    public TestData generateDataForSortingTests() {
        List<User> users = new ArrayList<>();

        User admin = new User("Admin_Sorted", "hash_admin", User.Role.ADMIN);
        User moderator = new User("Moderator_Sorted", "hash_mod", User.Role.MODERATOR);
        User user1 = new User("User_A_Sorted", "hash_user1", User.Role.USER);
        User user2 = new User("User_B_Sorted", "hash_user2", User.Role.USER);
        User guest = new User("Guest_Sorted", "hash_guest", User.Role.GUEST);

        users.add(admin);
        users.add(moderator);
        users.add(user1);
        users.add(user2);
        users.add(guest);

        List<Function> functions = new ArrayList<>();

        Function linearFunc = new Function(admin, "Linear_A", "linear", "2x + 1");
        Function quadraticFunc = new Function(moderator, "Quadratic_B", "quadratic", "x^2 - 4");
        Function cubicFunc = new Function(user1, "Cubic_C", "cubic", "x^3 - 3x");
        Function expFunc = new Function(user2, "Exponential_D", "exponential", "e^x");
        Function logFunc = new Function(guest, "Logarithmic_E", "logarithmic", "ln(x)");

        functions.add(linearFunc);
        functions.add(quadraticFunc);
        functions.add(cubicFunc);
        functions.add(expFunc);
        functions.add(logFunc);

        List<Point> points = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            double x = i;
            double y = 2 * x + 1;
            points.add(createPoint(linearFunc, x, y));
        }

        for (int i = -5; i <= 5; i++) {
            double x = i;
            double y = x * x - 4;
            points.add(createPoint(quadraticFunc, x, y));
        }

        for (double x = -3; x <= 3; x += 0.5) {
            double y = x * x * x - 3 * x;
            points.add(createPoint(cubicFunc, x, y));
        }

        for (double x = 0; x <= 3; x += 0.3) {
            double y = Math.exp(x);
            points.add(createPoint(expFunc, x, y));
        }

        for (double x = 1; x <= 10; x += 0.9) {
            double y = Math.log(x);
            points.add(createPoint(logFunc, x, y));
        }

        log.info("Generated sorting test data: {} users, {} functions, {} points",
                users.size(), functions.size(), points.size());

        return new TestData(users, functions, points);
    }

    public static class TestData {
        public final List<User> users;
        public final List<Function> functions;
        public final List<Point> points;

        public TestData(List<User> users, List<Function> functions, List<Point> points) {
            this.users = users;
            this.functions = functions;
            this.points = points;
        }

        public TestData(List<User> users, List<Function> functions) {
            this(users, functions, new ArrayList<>());
        }

        public int getTotalPoints() {
            return points.size();
        }

        public void printStatistics() {
            System.out.println("=== Test Data Statistics ===");
            System.out.println("Users: " + users.size());
            System.out.println("Functions: " + functions.size());
            System.out.println("Points: " + points.size());

            System.out.println("\nFunctions by type:");
            functions.stream()
                    .collect(java.util.stream.Collectors.groupingBy(Function::getType, java.util.stream.Collectors.counting()))
                    .forEach((type, count) -> System.out.println("  " + type + ": " + count));

            System.out.println("\nFunctions per user:");
            users.stream()
                    .collect(java.util.stream.Collectors.groupingBy(User::getName, java.util.stream.Collectors.counting()))
                    .forEach((name, count) -> System.out.println("  " + name + ": " + count + " functions"));

            if (!points.isEmpty()) {
                double minX = points.stream().mapToDouble(p -> p.getXVal().doubleValue()).min().orElse(0);
                double maxX = points.stream().mapToDouble(p -> p.getXVal().doubleValue()).max().orElse(0);
                double minY = points.stream().mapToDouble(p -> p.getYVal().doubleValue()).min().orElse(0);
                double maxY = points.stream().mapToDouble(p -> p.getYVal().doubleValue()).max().orElse(0);

                System.out.println("\nPoints range:");
                System.out.println("  X: [" + minX + ", " + maxX + "]");
                System.out.println("  Y: [" + minY + ", " + maxY + "]");
            }
        }
    }
}