package concurrent;

import functions.ArrayTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class SynchronizedTabulatedFunctionTest {
    @Test
    public void testGetCount() {
        double[] x = {0, 1, 2};
        double[] y = {1, 2, 3};
        TabulatedFunction func = new ArrayTabulatedFunction(x, y);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(func);

        assertEquals(3, syncFunc.getCount());
    }

    @Test
    public void testGetXandGetY() {
        double[] x = {0, 1, 2, 3};
        double[] y = {0, 1, 4, 9};
        TabulatedFunction func = new ArrayTabulatedFunction(x, y);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(func);

        assertEquals(0.0, syncFunc.getX(0));
        assertEquals(9.0, syncFunc.getY(3));
    }

    @Test
    public void testSetY() {
        double[] x = {0, 1, 2, 3};
        double[] y = {0, 1, 4, 9};
        TabulatedFunction func = new ArrayTabulatedFunction(x, y);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(func);

        syncFunc.setY(1, 10.5);

        assertEquals(10.5, syncFunc.getY(1));
        assertEquals(10.5, func.getY(1)); // проверка делегирования
    }

    @Test
    public void testIndexOfXandY() {
        double[] x = {0, 1, 2, 3};
        double[] y = {0, 1, 4, 9};
        TabulatedFunction func = new ArrayTabulatedFunction(x, y);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(func);

        assertEquals(2, syncFunc.indexOfX(2.0));
        assertEquals(3, syncFunc.indexOfY(9.0));
    }

    @Test
    public void testBounds() {
        double[] x = {10, 20, 30};
        double[] y = {100, 200, 300};
        TabulatedFunction func = new ArrayTabulatedFunction(x, y);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(func);

        assertEquals(10.0, syncFunc.leftBound());
        assertEquals(30.0, syncFunc.rightBound());
    }

    @Test
    public void testIterator() {
        double[] x = {0.0, 1.0, 2.0, 3.0};
        double[] y = {0.0, 1.0, 4.0, 9.0};
        TabulatedFunction func = new ArrayTabulatedFunction(x, y);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(func);

        Iterator<Point> iterator = syncFunc.iterator();

        int i = 0;
        while (iterator.hasNext()) {
            Point p = iterator.next();
            assertEquals(x[i], p.x);
            assertEquals(y[i], p.y);
            i++;
        }
        assertEquals(4, i);
    }
}