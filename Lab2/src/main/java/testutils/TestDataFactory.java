package testutils;

import java.util.Random;
import java.util.UUID;

public class TestDataFactory {

    private static final Random rnd = new Random(123); // фиксируем seed для воспроизводимости

    public static String randomUserName() {
        return "user_" + rnd.nextInt(1_000_000);
    }

    public static String randomPassword() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String randomRole() {
        String[] roles = {"user", "admin"};
        return roles[rnd.nextInt(roles.length)];
    }

    public static String randomFunctionName() {
        return "func_" + rnd.nextInt(1_000_000);
    }

    public static String randomFunctionType() {
        String[] types = { "TabulatedFunction", "MathFunction"};
        return types[rnd.nextInt(types.length)];
    }

    public static String randomExpression(String functionType) {
        if ("TabulatedFunction".equals(functionType)) {
            return "N/A"; // табулированная функция — нет выражения
        }
        // для MathFunction — обычная генерация
        return "x^2 + " + rnd.nextInt(10) + "*x + " + rnd.nextInt(10);
    }

//    public static String randomExpression() {
//        return randomExpression("MathFunction");
//    }

    public static double randomCoordinate() {
        return rnd.nextDouble() * 200 - 100; // от -100 до 100
    }
}

