package concurrent;

import functions.ArrayTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;

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
        assertTrue(iterator.hasNext());
        Point point1 = iterator.next();
        assertEquals(0.0, point1.x, 0.0001);
        assertEquals(0.0, point1.y, 0.0001);
        assertTrue(iterator.hasNext());
        Point point2 = iterator.next();
        assertEquals(1.0, point2.x, 0.0001);
        assertEquals(1.0, point2.y, 0.0001);
        assertTrue(iterator.hasNext());
        Point point3 = iterator.next();
        assertEquals(2.0, point3.x, 0.0001);
        assertEquals(4.0, point3.y, 0.0001);
        assertTrue(iterator.hasNext());
        Point point4 = iterator.next();
        assertEquals(3.0, point4.x, 0.0001);
        assertEquals(9.0, point4.y, 0.0001);
        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }
    @Test
    public void testIteratorRemove(){
        double[] x = {0.0, 1.0, 2.0, 3.0};
        double[] y = {0.0, 1.0, 4.0, 9.0};
        TabulatedFunction func = new ArrayTabulatedFunction(x, y);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(func);
        Iterator<Point> iterator = syncFunc.iterator();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void testDoSynchronouslyReturnValue() {
        double[] x = {0, 1, 2};
        double[] y = {0, 1, 4};
        TabulatedFunction baseFunc = new ArrayTabulatedFunction(x, y);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(baseFunc);

        Double result = syncFunc.doSynchronously(f -> {
            double y0 = f.getY(0);
            double y1 = f.getY(1);
            return y0 + y1;
        });

        assertEquals(1.0, result);
    }

    @Test
    void testDoSynchronouslyModifyAndReturnValue() {
        double[] x = {0, 1, 2};
        double[] y = {0, 1, 4};
        TabulatedFunction baseFunc = new ArrayTabulatedFunction(x, y);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(baseFunc);

        Double newValue = syncFunc.doSynchronously(f -> {
            f.setY(2, 10.0);
            return f.getY(2);
        });

        assertEquals(10.0, newValue);
        assertEquals(10.0, baseFunc.getY(2));
    }

    @Test
    void testDoSynchronouslyVoidOperation() {
        double[] x = {0, 1, 2};
        double[] y = {0, 1, 4};
        TabulatedFunction baseFunc = new ArrayTabulatedFunction(x, y);
        SynchronizedTabulatedFunction syncFunc = new SynchronizedTabulatedFunction(baseFunc);

        Void result = syncFunc.doSynchronously(new SynchronizedTabulatedFunction.Operation<Void>() {
            @Override
            public Void apply(SynchronizedTabulatedFunction f) {
                for (int i = 0; i < f.getCount(); i++) {
                    f.setY(i, f.getY(i) + 1);
                }
                return null;
            }
        });

        assertEquals(1.0, baseFunc.getY(0));
        assertEquals(2.0, baseFunc.getY(1));
        assertEquals(5.0, baseFunc.getY(2));
        assertNull(result);
    }
}