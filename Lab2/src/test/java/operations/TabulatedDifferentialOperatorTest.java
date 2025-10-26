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
    void testDeriveDivisionByZero() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        double[] xValues = {1.0, 1.0, 2.0}; // одинаковые x создадут dx = 0
        double[] yValues = {1.0, 2.0, 3.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(ArithmeticException.class, () -> operator.derive(function));
    }
}