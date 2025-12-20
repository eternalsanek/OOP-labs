package ru.ssau.tk.NAME.PROJECT.functions.factory;

import ru.ssau.tk.NAME.PROJECT.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.NAME.PROJECT.functions.TabulatedFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionFactoryTest {

    @Test
    void testCreate() {
        LinkedListTabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        // Проверяем, что созданный объект соответствует типу LinkedListTabulatedFunction
        assertTrue(function instanceof LinkedListTabulatedFunction);

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
        LinkedListTabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();

        double[] xValues = {0.0, 0.5, 1.0};
        double[] yValues = {0.0, 0.25, 1.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        // Проверяем тип созданного объекта
        assertTrue(function instanceof LinkedListTabulatedFunction);

        // Проверяем корректность данных
        assertEquals(3, function.getCount());
        assertEquals(0.0, function.leftBound(), 1e-12);
        assertEquals(1.0, function.rightBound(), 1e-12);
    }
}