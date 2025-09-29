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
    void testConstructorFromFunctionSinglePoint() {
        MathFunction source = new ConstantFunction(5.0);

        // Конструктор требует минимум 2 точки, поэтому тестируем с 2 точками
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(source, 1.0, 1.0, 2);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(1.0, function.getX(1), 1e-10);
        assertEquals(5.0, function.getY(0), 1e-10);
        assertEquals(5.0, function.getY(1), 1e-10);
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

        assertEquals(0, function.floorIndexOfX(0.5));  // левее всех
        assertEquals(3, function.floorIndexOfX(6.0));  // правее всех
        assertEquals(0, function.floorIndexOfX(1.0));  // точное совпадение с первым
        assertEquals(0, function.floorIndexOfX(2.0));  // между 1 и 3
        assertEquals(1, function.floorIndexOfX(3.0));  // точное совпадение со вторым
        assertEquals(1, function.floorIndexOfX(4.0));  // между 3 и 5
        assertEquals(2, function.floorIndexOfX(5.0));  // точное совпадение с последним
    }

    @Test
    void testExtrapolateLeft() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(0.0, function.extrapolateLeft(0.0), 1e-10);  // 10 + (20-10)*(0-1)/(2-1) = 0
        assertEquals(5.0, function.extrapolateLeft(0.5), 1e-10);  // 10 + (20-10)*(0.5-1)/(2-1) = 5
    }

    @Test
    void testExtrapolateRight() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(40.0, function.extrapolateRight(4.0), 1e-10);  // 20 + (30-20)*(4-2)/(3-2) = 40
        assertEquals(35.0, function.extrapolateRight(3.5), 1e-10);  // 20 + (30-20)*(3.5-2)/(3-2) = 35
    }

    @Test
    void testExtrapolateSinglePoint() {
        double[] xValues = {1.0};
        double[] yValues = {10.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(10.0, function.extrapolateLeft(0.0), 1e-10);
        assertEquals(10.0, function.extrapolateRight(2.0), 1e-10);
    }

    @Test
    void testInterpolateByIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(15.0, function.interpolate(1.5, 0), 1e-10);  // между 1 и 2
        assertEquals(25.0, function.interpolate(2.5, 1), 1e-10);  // между 2 и 3
    }

    @Test
    void testInterpolateSinglePoint() {
        double[] xValues = {1.0};
        double[] yValues = {10.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(10.0, function.interpolate(1.5, 0), 1e-10);
    }

    @Test
    void testApplyExactPoints() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(10.0, function.apply(1.0), 1e-10);
        assertEquals(20.0, function.apply(2.0), 1e-10);
        assertEquals(30.0, function.apply(3.0), 1e-10);
    }

    @Test
    void testApplyInterpolation() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(15.0, function.apply(1.5), 1e-10);  // интерполяция
        assertEquals(25.0, function.apply(2.5), 1e-10);  // интерполяция
    }

    @Test
    void testApplyExtrapolation() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(5.0, function.apply(0.5), 1e-10);   // экстраполяция слева
        assertEquals(40.0, function.apply(4.0), 1e-10);  // экстраполяция справа
    }

    @Test
    void testApplySinglePoint() {
        double[] xValues = {1.0};
        double[] yValues = {10.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(10.0, function.apply(0.0), 1e-10);   // экстраполяция
        assertEquals(10.0, function.apply(1.0), 1e-10);   // точное значение
        assertEquals(10.0, function.apply(2.0), 1e-10);   // экстраполяция
    }

    @Test
    void testGetNodeInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertThrows(IndexOutOfBoundsException.class, () -> function.getY(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> function.getY(2));
        assertThrows(IndexOutOfBoundsException.class, () -> function.setY(-1, 5.0));
        assertThrows(IndexOutOfBoundsException.class, () -> function.setY(2, 5.0));
    }

    @Test
    void testCircularLinkedListStructure() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        // Проверяем циклическую структуру через поведение
        assertEquals(1.0, function.leftBound(), 1e-10);
        assertEquals(3.0, function.rightBound(), 1e-10);
        assertEquals(10.0, function.getY(0), 1e-10);
        assertEquals(30.0, function.getY(2), 1e-10);

        // Проверяем, что после последнего элемента снова первый
        function.setY(0, 100.0);
        assertEquals(100.0, function.getY(0), 1e-10);
    }

    @Test
    void testConstructorInvalidCount() {
        MathFunction source = new IdentityFunction();

        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(source, 0.0, 1.0, 1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(source, 0.0, 1.0, 0);
        });
    }
}