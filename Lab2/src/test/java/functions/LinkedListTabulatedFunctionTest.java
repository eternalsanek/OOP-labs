package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionTest {

    @Test
    void testConstructorFromArrays() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.leftBound(), 1e-10);
        assertEquals(3.0, function.rightBound(), 1e-10);
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(2.0, function.getX(1), 1e-10);
        assertEquals(3.0, function.getX(2), 1e-10);
        assertEquals(10.0, function.getY(0), 1e-10);
        assertEquals(20.0, function.getY(1), 1e-10);
        assertEquals(30.0, function.getY(2), 1e-10);
    }

    @Test
    void testConstructorFromFunction() {
        MathFunction source = new SqrFunction();
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(source, 0.0, 2.0, 3);

        assertEquals(3, function.getCount());
        assertEquals(0.0, function.getX(0), 1e-10);
        assertEquals(1.0, function.getX(1), 1e-10);
        assertEquals(2.0, function.getX(2), 1e-10);
        assertEquals(0.0, function.getY(0), 1e-10);
        assertEquals(1.0, function.getY(1), 1e-10);
        assertEquals(4.0, function.getY(2), 1e-10);
    }

    @Test
    void testConstructorFromFunctionReversedBounds() {
        MathFunction source = new SqrFunction();
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(source, 2.0, 0.0, 3);

        assertEquals(3, function.getCount());
        assertEquals(0.0, function.getX(0), 1e-10);
        assertEquals(1.0, function.getX(1), 1e-10);
        assertEquals(2.0, function.getX(2), 1e-10);
    }

    @Test
    void testGetSetY() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.setY(1, 25.0);
        assertEquals(25.0, function.getY(1), 1e-10);
        assertEquals(10.0, function.getY(0), 1e-10);
        assertEquals(30.0, function.getY(2), 1e-10);
    }

    @Test
    void testIndexOfX() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfX(1.0));
        assertEquals(1, function.indexOfX(2.0));
        assertEquals(2, function.indexOfX(3.0));
        assertEquals(-1, function.indexOfX(0.0));
        assertEquals(-1, function.indexOfX(4.0));
        assertEquals(-1, function.indexOfX(1.5));
    }

    @Test
    void testIndexOfXWithPrecision() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // В пределах погрешности
        assertEquals(1, function.indexOfX(2.0 + 1e-13));
        // За пределами погрешности
        assertEquals(-1, function.indexOfX(2.0 + 1e-10));
        // Точное совпадение
        assertEquals(0, function.indexOfX(1.0));
    }

    @Test
    void testIndexOfY() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfY(10.0));
        assertEquals(1, function.indexOfY(20.0));
        assertEquals(2, function.indexOfY(30.0));
        assertEquals(-1, function.indexOfY(15.0));
        assertEquals(-1, function.indexOfY(40.0));
    }

    @Test
    void testIndexOfYWithPrecision() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // В пределах погрешности
        assertEquals(1, function.indexOfY(20.0 + 1e-13));
        // За пределами погрешности
        assertEquals(-1, function.indexOfY(20.0 + 1e-10));
        // Точное совпадение
        assertEquals(0, function.indexOfY(10.0));
    }

    @Test
    void testLeftRightBound() {
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {5.0, 15.0, 25.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0.5, function.leftBound(), 1e-10);
        assertEquals(2.5, function.rightBound(), 1e-10);
    }

    @Test
    void testFloorIndexOfX() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {10.0, 30.0, 50.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(3, function.floorIndexOfX(6.0));  // правее всех
        assertEquals(0, function.floorIndexOfX(1.0));  // точное совпадение с первым
        assertEquals(0, function.floorIndexOfX(2.0));  // между 1 и 3
        assertEquals(1, function.floorIndexOfX(3.0));  // точное совпадение со вторым
        assertEquals(1, function.floorIndexOfX(4.0));  // между 3 и 5
        assertEquals(2, function.floorIndexOfX(5.0));  // точное совпадение с последним
    }

    @Test
    void testFloorIndexOfXEdgeCases() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Граничные значения
        assertEquals(0, function.floorIndexOfX(1.0 + 1e-13)); // чуть больше первого
        assertEquals(3, function.floorIndexOfX(3.0 + 1e-13)); // чуть больше последнего
        assertEquals(1, function.floorIndexOfX(2.0 + 1e-13)); // чуть меньше последнего, но больше предпоследнего
    }

    @Test
    void testExtrapolateLeft() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0.0, function.extrapolateLeft(0.0), 1e-10);
        assertEquals(5.0, function.extrapolateLeft(0.5), 1e-10);
    }

    @Test
    void testExtrapolateRight() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(40.0, function.extrapolateRight(4.0), 1e-10);
        assertEquals(35.0, function.extrapolateRight(3.5), 1e-10);
    }

    @Test
    void testInterpolateByIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(15.0, function.interpolate(1.5, 0), 1e-10);
        assertEquals(25.0, function.interpolate(2.5, 1), 1e-10);
    }

    @Test
    void testInterpolateDirect() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Прямой вызов protected методов
        assertEquals(15.0, function.interpolate(1.5, 0), 1e-10);
        assertEquals(25.0, function.interpolate(2.5, 1), 1e-10);
    }

    @Test
    void testApply() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(10.0, function.apply(1.0), 1e-10);
        assertEquals(20.0, function.apply(2.0), 1e-10);
        assertEquals(30.0, function.apply(3.0), 1e-10);
        assertEquals(15.0, function.apply(1.5), 1e-10);
        assertEquals(25.0, function.apply(2.5), 1e-10);
        assertEquals(5.0, function.apply(0.5), 1e-10);
        assertEquals(40.0, function.apply(4.0), 1e-10);
    }

    @Test
    void testCircularStructure() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Проверяем, что структура остается циклической после операций
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(3.0, function.getX(2), 1e-10);

        // После вставки структура должна оставаться циклической
        function.insert(4.0, 40.0);
        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(4.0, function.getX(3), 1e-10);

        // После удаления структура должна оставаться циклической
        function.remove(1);
        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(4.0, function.getX(2), 1e-10);
    }

    @Test
    void testRemove() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xValues, yValues);

        // Удаляем из середины
        list.remove(1);
        assertEquals(3, list.getCount());
        assertEquals(0.0, list.getX(0), 1e-12);
        assertEquals(2.0, list.getX(1), 1e-12);
        assertEquals(3.0, list.getX(2), 1e-12);

        // Удаляем первый элемент
        list.remove(0);
        assertEquals(2, list.getCount());
        assertEquals(2.0, list.getX(0), 1e-12);
        assertEquals(3.0, list.getX(1), 1e-12);
        assertEquals(2.0, list.leftBound(), 1e-12);

        // Удаляем последний элемент
        list.remove(1);
        assertEquals(1, list.getCount());
        assertEquals(2.0, list.getX(0), 1e-12);
        assertEquals(4.0, list.getY(0), 1e-12);
    }

    @Test
    void testRemoveSingleElement() {
        // Тест удаления, когда остается 1 элемент (невозможно из-за требований минимум 2 точки)
        // Но проверим поведение при удалении до 1 элемента
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xValues, yValues);

        list.remove(0);
        assertEquals(1, list.getCount());
        assertEquals(2.0, list.getX(0), 1e-12);
        assertEquals(20.0, list.getY(0), 1e-12);
    }

    @Test
    void testInsert() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(x, y);

        // Вставка в начало
        list.insert(0.0, -1.0);
        assertEquals(4, list.getCount());
        assertEquals(0.0, list.getX(0), 1e-12);
        assertEquals(-1.0, list.getY(0), 1e-12);

        // Вставка в конец
        list.insert(4.0, 16.0);
        assertEquals(5, list.getCount());
        assertEquals(4.0, list.getX(4), 1e-12);
        assertEquals(16.0, list.getY(4), 1e-12);

        // Вставка в середину
        list.insert(2.5, 6.25);
        assertEquals(6, list.getCount());
        assertEquals(2.5, list.getX(3), 1e-12); // позиция после вставок
        assertEquals(6.25, list.getY(3), 1e-12);
    }

    @Test
    void testInsertWithExactXMatch() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(x, y);

        // Вставка с существующим X (должна обновить Y)
        list.insert(2.0, 8.0);
        assertEquals(3, list.getCount()); // количество не должно измениться
        assertEquals(8.0, list.getY(1), 1e-12); // значение должно обновиться
    }

    @Test
    void testInsertWithPrecision() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(x, y);

        // Вставка с X очень близким к существующему (в пределах погрешности)
        list.insert(2.0 + 1e-13, 8.0);
        assertEquals(3, list.getCount()); // должно распознаться как существующий X
        assertEquals(8.0, list.getY(1), 1e-12);
    }

    @Test
    void testInsertIntoMiddlePrecise() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {1.0, 9.0, 25.0};
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xValues, yValues);

        // Точная вставка между существующими точками
        list.insert(2.0, 4.0);
        assertEquals(4, list.getCount());
        assertEquals(2.0, list.getX(1), 1e-12);
        assertEquals(4.0, list.getY(1), 1e-12);
        assertEquals(1.0, list.getX(0), 1e-12);
        assertEquals(3.0, list.getX(2), 1e-12);
    }

    // Тесты на исключения
    @Test
    void testConstructorWithLessThan2Points() {
        double[] xValues = {1.0};
        double[] yValues = {10.0};

        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorWithEmptyArrays() {
        double[] xValues = {};
        double[] yValues = {};

        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorWithMathFunctionLessThan2Points() {
        MathFunction source = new IdentityFunction();

        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(source, 0.0, 1.0, 1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(source, 0.0, 1.0, 0);
        });
    }

    @Test
    void testGetXInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getX(2));
        assertThrows(IllegalArgumentException.class, () -> function.getX(10));
    }

    @Test
    void testGetYInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getY(2));
        assertThrows(IllegalArgumentException.class, () -> function.getY(100));
    }

    @Test
    void testSetYInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.setY(-1, 5.0));
        assertThrows(IllegalArgumentException.class, () -> function.setY(2, 5.0));
        assertThrows(IllegalArgumentException.class, () -> function.setY(-5, 5.0));
    }

    @Test
    void testRemoveInvalidIndex() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};
        LinkedListTabulatedFunction list = new LinkedListTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> list.remove(-1));
        assertThrows(IllegalArgumentException.class, () -> list.remove(3));
        assertThrows(IllegalArgumentException.class, () -> list.remove(5));
    }

    @Test
    void testFloorIndexOfXLessThanLeftBound() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.floorIndexOfX(0.5));
        assertThrows(IllegalArgumentException.class, () -> function.floorIndexOfX(-1.0));
    }

    @Test
    void testGetNodeInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // getNode - приватный метод, но он используется в других методах
        // Проверяем через публичные методы, которые его используют
        assertThrows(IllegalArgumentException.class, () -> function.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getY(2));
    }
    @Test
    void testIteratorWhileLoop() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        var iterator = function.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(xValues[index], point.x, 1e-10);
            assertEquals(yValues[index], point.y, 1e-10);
            index++;
        }
        assertEquals(3, index); // Проверяем, что прошли по всем точкам
    }

    @Test
    void testIteratorForEachLoop() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        int index = 0;
        for (Point point : function) {
            assertEquals(xValues[index], point.x, 1e-10);
            assertEquals(yValues[index], point.y, 1e-10);
            index++;
        }
        assertEquals(3, index); // Проверяем, что прошли по всем точкам
    }

    @Test
    void testIteratorOnSingleElement() {
        // Тест на функции с минимальным количеством точек (2 точки)
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Тестируем while цикл
        var iterator = function.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(xValues[count], point.x, 1e-10);
            assertEquals(yValues[count], point.y, 1e-10);
            count++;
        }
        assertEquals(2, count);

        // Тестируем for-each цикл
        count = 0;
        for (Point point : function) {
            assertEquals(xValues[count], point.x, 1e-10);
            assertEquals(yValues[count], point.y, 1e-10);
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void testIteratorAfterModification() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Модифицируем функцию
        function.setY(1, 25.0);
        function.insert(1.5, 15.0);

        // Ожидаемые значения после модификации
        double[] expectedX = {1.0, 1.5, 2.0, 3.0};
        double[] expectedY = {10.0, 15.0, 25.0, 30.0};

        // Проверяем итератором while
        var iterator = function.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(expectedX[index], point.x, 1e-10);
            assertEquals(expectedY[index], point.y, 1e-10);
            index++;
        }
        assertEquals(4, index);

        // Проверяем for-each
        index = 0;
        for (Point point : function) {
            assertEquals(expectedX[index], point.x, 1e-10);
            assertEquals(expectedY[index], point.y, 1e-10);
            index++;
        }
        assertEquals(4, index);
    }

    @Test
    void testMultipleIterators() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Создаем два независимых итератора
        var iterator1 = function.iterator();
        var iterator2 = function.iterator();

        // Используем первый итератор
        int count1 = 0;
        while (iterator1.hasNext()) {
            Point point = iterator1.next();
            assertEquals(xValues[count1], point.x, 1e-10);
            assertEquals(yValues[count1], point.y, 1e-10);
            count1++;
        }

        // Используем второй итератор
        int count2 = 0;
        while (iterator2.hasNext()) {
            Point point = iterator2.next();
            assertEquals(xValues[count2], point.x, 1e-10);
            assertEquals(yValues[count2], point.y, 1e-10);
            count2++;
        }

        assertEquals(3, count1);
        assertEquals(3, count2);
    }
}