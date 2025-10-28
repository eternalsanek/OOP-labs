package functions.factory;

import functions.ArrayTabulatedFunction;
import functions.TabulatedFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionFactoryTest {

    @Test
    void testCreate() {
        ArrayTabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        // Проверяем, что созданный объект соответствует типу ArrayTabulatedFunction
        assertTrue(function instanceof ArrayTabulatedFunction);

        // Проверяем, что функция корректно создана с правильными значениями
        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-12);
        assertEquals(2.0, function.getX(1), 1e-12);
        assertEquals(3.0, function.getX(2), 1e-12);
        assertEquals(10.0, function.getY(0), 1e-12);
        assertEquals(20.0, function.getY(1), 1e-12);
        assertEquals(30.0, function.getY(2), 1e-12);
    }

    @Test
    void testCreateWithDifferentData() {
        ArrayTabulatedFunctionFactory factory = new ArrayTabulatedFunctionFactory();

        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        // Проверяем тип созданного объекта
        assertTrue(function instanceof ArrayTabulatedFunction);

        // Проверяем корректность данных
        assertEquals(3, function.getCount());
        assertEquals(0.0, function.leftBound(), 1e-12);
        assertEquals(1.0, function.rightBound(), 1e-12);
    }
}