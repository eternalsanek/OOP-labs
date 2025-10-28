package operations;

import functions.*;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedDifferentialOperatorTest {

    @Test
    void testDeriveWithArrayFactory() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0}; // f(x) = x^2
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.derive(function);

        // Проверяем тип созданной функции
        assertTrue(derivative instanceof ArrayTabulatedFunction);

        // Проверяем значения производной: f'(x) = 2x
        assertEquals(3, derivative.getCount());
        assertEquals(0.0, derivative.getX(0), 1e-12);
        assertEquals(1.0, derivative.getX(1), 1e-12);
        assertEquals(2.0, derivative.getX(2), 1e-12);

        assertEquals(1.0, derivative.getY(0), 1e-12);  // (1-0)/(1-0) = 1
        assertEquals(3.0, derivative.getY(1), 1e-12);  // (4-1)/(2-1) = 3
        assertEquals(3.0, derivative.getY(2), 1e-12);  // последняя = предпоследняя
    }

    @Test
    void testDeriveWithLinkedListFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 3.0}; // f(x) = x
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.derive(function);

        // Проверяем тип созданной функции
        assertTrue(derivative instanceof LinkedListTabulatedFunction);

        // Проверяем значения производной: f'(x) = 1
        assertEquals(3, derivative.getCount());
        assertEquals(1.0, derivative.getY(0), 1e-12);  // (2-1)/(2-1) = 1
        assertEquals(1.0, derivative.getY(1), 1e-12);  // (3-2)/(3-2) = 1
        assertEquals(1.0, derivative.getY(2), 1e-12);  // последняя = предпоследняя
    }

    @Test
    void testDeriveConstantFunction() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {5.0, 5.0, 5.0}; // f(x) = 5
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.derive(function);

        // Производная константы = 0
        assertEquals(0.0, derivative.getY(0), 1e-12);
        assertEquals(0.0, derivative.getY(1), 1e-12);
        assertEquals(0.0, derivative.getY(2), 1e-12);
    }

    @Test
    void testDeriveLinearFunction() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {2.0, 4.0, 6.0}; // f(x) = 2x + 2
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.derive(function);

        // Производная линейной функции = константа (2)
        assertEquals(2.0, derivative.getY(0), 1e-12);  // (4-2)/(1-0) = 2
        assertEquals(2.0, derivative.getY(1), 1e-12);  // (6-4)/(2-1) = 2
        assertEquals(2.0, derivative.getY(2), 1e-12);  // последняя = предпоследняя
    }

    @Test
    void testConstructorWithFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        assertEquals(factory, operator.getFactory());
    }

    @Test
    void testDefaultConstructor() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        assertTrue(operator.getFactory() instanceof ArrayTabulatedFunctionFactory);
    }

    @Test
    void testSetFactory() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
        TabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();

        operator.setFactory(newFactory);

        assertEquals(newFactory, operator.getFactory());
    }

    @Test
    void testDeriveWithTwoPoints() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {0.0, 1.0};
        double[] yValues = {0.0, 1.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.derive(function);

        assertEquals(2, derivative.getCount());
        assertEquals(1.0, derivative.getY(0), 1e-12);  // (1-0)/(1-0) = 1
        assertEquals(1.0, derivative.getY(1), 1e-12);  // последняя = предпоследняя
    }

    @Test
    void testDeriveWithSmallStep() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        // Используем небольшой шаг для проверки точности
        double[] xValues = {1.0, 1.1, 2.0};
        double[] yValues = {1.0, 2.0, 3.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.derive(function);

        // Проверяем, что производная вычисляется корректно
        assertEquals(3, derivative.getCount());

        // Первая производная: (2.0 - 1.0) / (1.1 - 1.0) = 1.0 / 0.1 = 10.0
        assertEquals(10.0, derivative.getY(0), 1e-12);

        // Вторая производная: (3.0 - 2.0) / (2.0 - 1.1) = 1.0 / 0.9 ≈ 1.1111111111111112
        assertEquals(1.0 / 0.9, derivative.getY(1), 1e-12);

        // Последняя производная равна предпоследней
        assertEquals(derivative.getY(1), derivative.getY(2), 1e-12);
    }

    @Test
    void testDeriveWithSinglePointShouldThrow() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        // Функция с одной точкой не может быть продифференцирована
        double[] xValues = {1.0};
        double[] yValues = {2.0};

        // Создание функции с одной точкой должно бросать исключение из-за минимальной длины
        assertThrows(IllegalArgumentException.class, () -> {
            TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testDeriveWithNonUniformGrid() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {0.0, 0.5, 2.0}; // неравномерная сетка
        double[] yValues = {0.0, 0.25, 4.0}; // f(x) = x^2
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.derive(function);

        assertEquals(3, derivative.getCount());

        // Первая производная: (0.25 - 0.0) / (0.5 - 0.0) = 0.5
        assertEquals(0.5, derivative.getY(0), 1e-12);

        // Вторая производная: (4.0 - 0.25) / (2.0 - 0.5) = 3.75 / 1.5 = 2.5
        assertEquals(2.5, derivative.getY(1), 1e-12);

        // Последняя производная равна предпоследней
        assertEquals(2.5, derivative.getY(2), 1e-12);
    }

    @Test
    void testDeriveWithNegativeValues() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {-2.0, -1.0, 0.0};
        double[] yValues = {4.0, 1.0, 0.0}; // f(x) = x^2
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedFunction derivative = operator.derive(function);

        assertEquals(3, derivative.getCount());

        // Первая производная: (1.0 - 4.0) / (-1.0 - (-2.0)) = -3.0 / 1.0 = -3.0
        assertEquals(-3.0, derivative.getY(0), 1e-12);

        // Вторая производная: (0.0 - 1.0) / (0.0 - (-1.0)) = -1.0 / 1.0 = -1.0
        assertEquals(-1.0, derivative.getY(1), 1e-12);

        // Последняя производная равна предпоследней
        assertEquals(-1.0, derivative.getY(2), 1e-12);
    }
}