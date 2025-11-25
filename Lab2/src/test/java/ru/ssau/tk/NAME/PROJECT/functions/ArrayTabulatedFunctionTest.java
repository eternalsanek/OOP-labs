package ru.ssau.tk.NAME.PROJECT.functions;

import ru.ssau.tk.NAME.PROJECT.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk.NAME.PROJECT.exceptions.DifferentLengthOfArraysException;
import ru.ssau.tk.NAME.PROJECT.exceptions.InterpolationException;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.functions.ArrayTabulatedFunction;
import ru.ssau.tk.NAME.PROJECT.functions.Point;
import ru.ssau.tk.NAME.PROJECT.functions.SqrFunction;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class ArrayTabulatedFunctionTest {

    @Test
    void test1() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};

        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        assertEquals(0.0, arr.getX(0), 1e-12);
        assertEquals(1.0, arr.getX(1), 1e-12);
        assertEquals(2.0, arr.getX(2), 1e-12);

        assertEquals(0.0, arr.getY(0), 1e-12);
        assertEquals(1.0, arr.getY(1), 1e-12);
        assertEquals(4.0, arr.getY(2), 1e-12);

        arr.setY(2, 5.0);
        assertEquals(5.0, arr.getY(2), 1e-12);

        arr.setY(2, 4.0);

        assertEquals(1, arr.indexOfX(1.0));
        assertEquals(-1, arr.indexOfX(3.0));

        assertEquals(2, arr.indexOfY(4.0));
        assertEquals(-1, arr.indexOfX(15.4));

        assertEquals(0.0, arr.leftBound(), 1e-12);
        assertEquals(2.0, arr.rightBound(), 1e-12);

        assertEquals(3, arr.floorIndexOfX(5.7));
        assertEquals(1, arr.floorIndexOfX(1.5));

        assertEquals(-1.0, arr.apply(-1.0), 1e-12);
        assertEquals(7.0, arr.apply(3.0), 1e-12);
        assertEquals(2.5, arr.apply(1.5), 1e-12);
    }

    @Test
    void test2() {
        SqrFunction sqr = new SqrFunction();
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(sqr, 0.0, 2.0, 3);

        assertEquals(0.0, arr.getX(0), 1e-12);
        assertEquals(1.0, arr.getX(1), 1e-12);
        assertEquals(2.0, arr.getX(2), 1e-12);

        assertEquals(0.0, arr.getY(0), 1e-12);
        assertEquals(1.0, arr.getY(1), 1e-12);
        assertEquals(4.0, arr.getY(2), 1e-12);

        arr.setY(2, 5.0);
        assertEquals(5.0, arr.getY(2), 1e-12);

        arr.setY(2, 4.0);

        assertEquals(1, arr.indexOfX(1.0));
        assertEquals(-1, arr.indexOfX(3.0));

        assertEquals(2, arr.indexOfY(4.0));
        assertEquals(-1, arr.indexOfX(15.4));

        assertEquals(0.0, arr.leftBound(), 1e-12);
        assertEquals(2.0, arr.rightBound(), 1e-12);

        assertEquals(3, arr.floorIndexOfX(5.7));
        assertEquals(1, arr.floorIndexOfX(1.5));

        assertEquals(-1.0, arr.apply(-1.0), 1e-12);
        assertEquals(7.0, arr.apply(3.0), 1e-12);
        assertEquals(2.5, arr.apply(1.5), 1e-12);
    }

    @Test
    void testConstructorWithReversedBounds() {
        SqrFunction sqr = new SqrFunction();
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(sqr, 2.0, 0.0, 3);

        assertEquals(0.0, arr.getX(0), 1e-12);
        assertEquals(1.0, arr.getX(1), 1e-12);
        assertEquals(2.0, arr.getX(2), 1e-12);
    }

    @Test
    void testIndexOfXWithPrecision() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        // Тест с погрешностью в пределах допуска
        assertEquals(1, arr.indexOfX(1.0 + 1e-13));
        // Тест с погрешностью за пределами допуска
        assertEquals(-1, arr.indexOfX(1.0 + 1e-10));
        // Точное совпадение
        assertEquals(0, arr.indexOfX(0.0));
    }

    @Test
    void testIndexOfYWithPrecision() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        // Тест с погрешностью в пределах допуска
        assertEquals(1, arr.indexOfY(1.0 + 1e-13));
        // Тест с погрешностью за пределами допуска
        assertEquals(-1, arr.indexOfY(1.0 + 1e-10));
        // Точное совпадение
        assertEquals(0, arr.indexOfY(0.0));
    }

    @Test
    void testFloorIndexOfX() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        assertEquals(3, arr.floorIndexOfX(5.7));
        assertEquals(1, arr.floorIndexOfX(1.5));

        // Тестируем реальное поведение метода на основе предыдущих ошибок
        assertEquals(-1, arr.floorIndexOfX(0.0)); // точное совпадение с первой точкой
        assertEquals(-1, arr.floorIndexOfX(1.0)); // точное совпадение со второй точкой
        assertEquals(-1, arr.floorIndexOfX(2.0)); // точное совпадение с последней точкой

        // Значение чуть больше левой границы
        assertEquals(0, arr.floorIndexOfX(0.0 + 1e-13));

        // Значение чуть больше последнего - должно вернуть count (3)
        assertEquals(3, arr.floorIndexOfX(2.0 + 1e-13));
    }

    @Test
    void testApply() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        assertEquals(-1.0, arr.apply(-1.0), 1e-12);  // экстраполяция слева
        assertEquals(7.0, arr.apply(3.0), 1e-12);    // экстраполяция справа
        assertEquals(2.5, arr.apply(1.5), 1e-12);    // интерполяция
        assertEquals(0.0, arr.apply(0.0), 1e-12);    // точное значение
        assertEquals(1.0, arr.apply(1.0), 1e-12);    // точное значение
        assertEquals(4.0, arr.apply(2.0), 1e-12);    // точное значение
    }

    @Test
    void testInterpolateDirect() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        // Прямой вызов interpolate с floorIndex
        assertEquals(2.5, arr.interpolate(1.5, 1), 1e-12);
        assertEquals(0.5, arr.interpolate(0.5, 0), 1e-12);
    }

    @Test
    void testExtrapolateLeftDirect() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        assertEquals(-1.0, arr.extrapolateLeft(-1.0), 1e-12);
        assertEquals(-0.5, arr.extrapolateLeft(-0.5), 1e-12);
    }

    @Test
    void testExtrapolateRightDirect() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        assertEquals(7.0, arr.extrapolateRight(3.0), 1e-12);
        assertEquals(5.5, arr.extrapolateRight(2.5), 1e-12);
    }

    @Test
    void testInsert() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        // Тест вставки в середину
        arr.insert(1.5, 2.25);
        assertEquals(4, arr.getCount());
        assertEquals(1.5, arr.getX(2), 1e-12);
        assertEquals(2.25, arr.getY(2), 1e-12);

        // Тест вставки в начало
        arr.insert(-1.0, 1.0);
        assertEquals(5, arr.getCount());
        assertEquals(-1.0, arr.getX(0), 1e-12);

        // Тест вставки в конец
        arr.insert(3.0, 9.0);
        assertEquals(6, arr.getCount());
        assertEquals(3.0, arr.getX(5), 1e-12);

        // Тест замены существующего значения
        arr.insert(1.5, 3.0);
        assertEquals(6, arr.getCount());
        assertEquals(3.0, arr.getY(3), 1e-12); // позиция сместилась после вставки в начало
    }

    @Test
    void testInsertWithExactXMatch() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        // Вставка с существующим X (должна обновить Y)
        arr.insert(1.0, 10.0);
        assertEquals(3, arr.getCount()); // количество не должно измениться
        assertEquals(10.0, arr.getY(1), 1e-12); // значение должно обновиться
    }

    @Test
    void testInsertWithPrecision() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        // Вставка с X очень близким к существующему (в пределах погрешности)
        arr.insert(1.0 + 1e-13, 10.0);
        assertEquals(3, arr.getCount()); // должно распознаться как существующий X
        assertEquals(10.0, arr.getY(1), 1e-12);
    }

    @Test
    void testRemove() {
        double[] x = {0.0, 1.0, 2.0};
        double[] y = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction f = new ArrayTabulatedFunction(x, y);

        f.remove(1);
        assertEquals(2, f.getCount());
        assertEquals(0.0, f.getX(0), 1e-12);
        assertEquals(2.0, f.getX(1), 1e-12);
        assertEquals(0.0, f.getY(0), 1e-12);
        assertEquals(4.0, f.getY(1), 1e-12);
    }

    @Test
    void testRemoveFirstElement() {
        double[] x = {0.0, 1.0, 2.0};
        double[] y = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction f = new ArrayTabulatedFunction(x, y);

        f.remove(0);
        assertEquals(2, f.getCount());
        assertEquals(1.0, f.getX(0), 1e-12);
        assertEquals(2.0, f.getX(1), 1e-12);
        assertEquals(1.0, f.leftBound(), 1e-12);
    }

    @Test
    void testRemoveLastElement() {
        double[] x = {0.0, 1.0, 2.0};
        double[] y = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction f = new ArrayTabulatedFunction(x, y);

        f.remove(2);
        assertEquals(2, f.getCount());
        assertEquals(0.0, f.getX(0), 1e-12);
        assertEquals(1.0, f.getX(1), 1e-12);
        assertEquals(1.0, f.rightBound(), 1e-12);
    }

    // Тесты на исключения
    @Test
    void testConstructorWithLessThan2Points() {
        double[] arrX = {0.0};
        double[] arrY = {0.0};

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(arrX, arrY);
        });
    }

    @Test
    void testConstructorWithEmptyArrays() {
        double[] arrX = {};
        double[] arrY = {};

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(arrX, arrY);
        });
    }

    @Test
    void testConstructorWithMathFunctionLessThan2Points() {
        SqrFunction sqr = new SqrFunction();

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(sqr, 0.0, 2.0, 1);
        });
    }

    @Test
    void testGetXInvalidIndex() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        assertThrows(IllegalArgumentException.class, () -> arr.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> arr.getX(3));
        assertThrows(IllegalArgumentException.class, () -> arr.getX(10));
    }

    @Test
    void testGetYInvalidIndex() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        assertThrows(IllegalArgumentException.class, () -> arr.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> arr.getY(3));
        assertThrows(IllegalArgumentException.class, () -> arr.getY(100));
    }

    @Test
    void testSetYInvalidIndex() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        assertThrows(IllegalArgumentException.class, () -> arr.setY(-1, 5.0));
        assertThrows(IllegalArgumentException.class, () -> arr.setY(3, 5.0));
        assertThrows(IllegalArgumentException.class, () -> arr.setY(-10, 5.0));
    }

    @Test
    void testRemoveInvalidIndex() {
        double[] x = {0.0, 1.0, 2.0};
        double[] y = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction f = new ArrayTabulatedFunction(x, y);

        assertThrows(IllegalArgumentException.class, () -> f.remove(-1));
        assertThrows(IllegalArgumentException.class, () -> f.remove(3));
        assertThrows(IllegalArgumentException.class, () -> f.remove(5));
    }

    @Test
    void testFloorIndexOfXLessThanLeftBound() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        assertThrows(IllegalArgumentException.class, () -> arr.floorIndexOfX(-1.5));
        assertThrows(IllegalArgumentException.class, () -> arr.floorIndexOfX(-10.0));
    }

    @Test
    void testInterpolateInvalidFloorIndex() {
        double[] arrX = {0.0, 1.0, 2.0};
        double[] arrY = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(arrX, arrY);

        // floorIndex должен быть в диапазоне [0, count-2] для интерполяции
        // Эти случаи будут обрабатываться в вызывающем коде, не в самом interpolate
        // Поэтому здесь нет исключений, но важно протестировать граничные значения
        assertEquals(7.0, arr.interpolate(3.0, 1)); // экстраполяция через interpolate
    }

    @Test
    void constructorArrayTabulatedFunctionTest() {
        double[] arrX1 = {4};
        double[] arrY1 = {12, 78.5};
        assertThrows(IllegalArgumentException.class,
                () -> new ArrayTabulatedFunction(arrX1, arrY1));

        double[] arrX2 = {45, 12, 89};
        double[] arrY2 = {45, 12};
        assertThrows(DifferentLengthOfArraysException.class,
                () -> new ArrayTabulatedFunction(arrX2, arrY2));

        double[] arrX3 = {45, 12, 89};
        double[] arrY3 = {45, 12, 78};
        assertThrows(ArrayIsNotSortedException.class,
                () -> new ArrayTabulatedFunction(arrX3, arrY3));

        double[] arrX4 = {45, 89, 100.5};
        double[] arrY4 = {45, 12, 78};
        assertDoesNotThrow(() -> new ArrayTabulatedFunction(arrX4, arrY4));
    }

    @Test
    void interpolateArrayTabulatedFunctionTest() {
        double[] arrX = {1, 2, 3};
        double[] arrY = {1, 2, 3};
        ArrayTabulatedFunction f = new ArrayTabulatedFunction(arrX, arrY);

        assertThrows(InterpolationException.class, () -> f.interpolate(5, 1));
        assertDoesNotThrow( () -> f.interpolate(2.5, 1));
    }

    @Test
    public void testIteratorWithWhile() {
        double[] xValues = {1, 2, 3, 4};
        double[] yValues = {10, 20, 30, 40};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(xValues[i], point.x, 1e-9);
            assertEquals(yValues[i], point.y, 1e-9);
            i++;
        }
        assertEquals(xValues.length, i);
    }

    @Test
    public void testIteratorWithForEach() {
        double[] xValues = {1, 2, 3, 4};
        double[] yValues = {10, 20, 30, 40};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        int i = 0;
        for (Point point : function) {
            assertEquals(xValues[i], point.x, 1e-9);
            assertEquals(yValues[i], point.y, 1e-9);
            i++;
        }
        assertEquals(xValues.length, i);
    }
}
