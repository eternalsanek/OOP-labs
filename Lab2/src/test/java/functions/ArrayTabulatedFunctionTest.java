package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals(0, arr.floorIndexOfX(-1.5));
        assertEquals(3, arr.floorIndexOfX(5.7));
        assertEquals(1, arr.floorIndexOfX(1.5));

        assertEquals(-1.0, arr.apply(-1.0), 1e-12);
        assertEquals(7.0, arr.apply(3.0), 1e-12);
        assertEquals(2.5, arr.apply(1.5), 1e-12);
    }

    @Test
    public void test2() {
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

        assertEquals(0, arr.floorIndexOfX(-1.5));
        assertEquals(3, arr.floorIndexOfX(5.7));
        assertEquals(1, arr.floorIndexOfX(1.5));

        assertEquals(-1.0, arr.apply(-1.0), 1e-12);
        assertEquals(7.0, arr.apply(3.0), 1e-12);
        assertEquals(2.5, arr.apply(1.5), 1e-12);
    }

    @Test
    void test3() {
        MathFunction square = new SqrFunction();
        ArrayTabulatedFunction arr = new ArrayTabulatedFunction(square, 0.0, 2.0, 1);

        // Проверяем, что массивы заполнены одним значением
        assertEquals(1, arr.getCount());
        assertEquals(0.0, arr.getX(0), 1e-12);
        assertEquals(0.0, arr.getY(0), 1e-12);

        // Экстраполяция слева и справа должны возвращать то же значение
        assertEquals(0.0, arr.apply(-5.0), 1e-12);
        assertEquals(0.0, arr.apply(10.0), 1e-12);
    }
}
