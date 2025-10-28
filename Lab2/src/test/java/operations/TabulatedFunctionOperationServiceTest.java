package operations;

import exceptions.InconsistentFunctionsException;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TabulatedFunctionOperationServiceTest {
    @Test
    void testAsPointsWithArrayTabulatedFunction() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(xValues.length, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithLinkedListTabulatedFunction() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {2.0, 4.0, 6.0, 8.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(xValues.length, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testSumArrayFunctions() {
        double[] x = {1, 2, 3};
        double[] y1 = {10, 20, 30};
        double[] y2 = {1, 2, 3};

        TabulatedFunction f1 = new ArrayTabulatedFunction(x, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x, y2);

        TabulatedFunctionOperationService service =
                new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction result = service.sum(f1, f2);

        Point[] points = TabulatedFunctionOperationService.asPoints(result);
        for (int i = 0; i < x.length; i++) {
            assertEquals(x[i], points[i].x, 1e-9);
            assertEquals(y1[i] + y2[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testSubtractMixedTypes() {
        double[] x = {1, 2, 3};
        double[] y1 = {5, 7, 9};
        double[] y2 = {1, 2, 3};

        TabulatedFunction f1 = new LinkedListTabulatedFunction(x, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x, y2);

        TabulatedFunctionOperationService service =
                new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());

        TabulatedFunction result = service.subtract(f1, f2);

        Point[] points = TabulatedFunctionOperationService.asPoints(result);
        for (int i = 0; i < x.length; i++) {
            assertEquals(x[i], points[i].x, 1e-9);
            assertEquals(y1[i] - y2[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testInconsistentCountThrows() {
        double[] x1 = {1, 2, 3};
        double[] y1 = {1, 2, 3};
        double[] x2 = {1, 2};
        double[] y2 = {4, 5};

        TabulatedFunction f1 = new ArrayTabulatedFunction(x1, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x2, y2);

        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        assertThrows(InconsistentFunctionsException.class, () -> service.sum(f1, f2));
        assertThrows(InconsistentFunctionsException.class, () -> service.subtract(f1, f2));
    }

    @Test
    void testInconsistentXThrows() {
        double[] x1 = {1, 2, 3};
        double[] y1 = {1, 2, 3};
        double[] x2 = {1, 2.5, 3}; // разные x
        double[] y2 = {4, 5, 6};

        TabulatedFunction f1 = new ArrayTabulatedFunction(x1, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x2, y2);

        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        assertThrows(InconsistentFunctionsException.class, () -> service.sum(f1, f2));
        assertThrows(InconsistentFunctionsException.class, () -> service.subtract(f1, f2));
    }

    @Test
    void testFactoryGetterSetter() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        TabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();
        service.setFactory(newFactory);
        assertEquals(newFactory, service.getFactory());
    }

    @Test
    void testMultiplyArrayFunctions() {
        double[] x = {1, 2, 3};
        double[] y1 = {2, 3, 4};
        double[] y2 = {5, 6, 7};

        TabulatedFunction f1 = new ArrayTabulatedFunction(x, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x, y2);

        TabulatedFunctionOperationService service =
                new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());

        TabulatedFunction result = service.multiply(f1, f2);

        Point[] points = TabulatedFunctionOperationService.asPoints(result);
        for (int i = 0; i < x.length; i++) {
            assertEquals(x[i], points[i].x, 1e-9);
            assertEquals(y1[i] * y2[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testMultiplyLinkedListFunctions() {
        double[] x = {0.5, 1.0, 1.5, 2.0};
        double[] y1 = {1.0, 2.0, 3.0, 4.0};
        double[] y2 = {2.0, 3.0, 4.0, 5.0};

        TabulatedFunction f1 = new LinkedListTabulatedFunction(x, y1);
        TabulatedFunction f2 = new LinkedListTabulatedFunction(x, y2);

        TabulatedFunctionOperationService service =
                new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction result = service.multiply(f1, f2);

        Point[] points = TabulatedFunctionOperationService.asPoints(result);
        for (int i = 0; i < x.length; i++) {
            assertEquals(x[i], points[i].x, 1e-9);
            assertEquals(y1[i] * y2[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testMultiplyMixedTypes() {
        double[] x = {1, 2, 3, 4};
        double[] y1 = {2, 4, 6, 8};
        double[] y2 = {0.5, 1.0, 1.5, 2.0};

        TabulatedFunction f1 = new ArrayTabulatedFunction(x, y1);
        TabulatedFunction f2 = new LinkedListTabulatedFunction(x, y2);

        TabulatedFunctionOperationService service =
                new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());

        TabulatedFunction result = service.multiply(f1, f2);

        Point[] points = TabulatedFunctionOperationService.asPoints(result);
        for (int i = 0; i < x.length; i++) {
            assertEquals(x[i], points[i].x, 1e-9);
            assertEquals(y1[i] * y2[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testDivideArrayFunctions() {
        double[] x = {1, 2, 3};
        double[] y1 = {10, 20, 30};
        double[] y2 = {2, 4, 5};

        TabulatedFunction f1 = new ArrayTabulatedFunction(x, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x, y2);

        TabulatedFunctionOperationService service =
                new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction result = service.divide(f1, f2);

        Point[] points = TabulatedFunctionOperationService.asPoints(result);
        for (int i = 0; i < x.length; i++) {
            assertEquals(x[i], points[i].x, 1e-9);
            assertEquals(y1[i] / y2[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testDivideLinkedListFunctions() {
        double[] x = {1, 2, 3, 4};
        double[] y1 = {15, 25, 35, 45};
        double[] y2 = {3, 5, 7, 9};

        TabulatedFunction f1 = new LinkedListTabulatedFunction(x, y1);
        TabulatedFunction f2 = new LinkedListTabulatedFunction(x, y2);

        TabulatedFunctionOperationService service =
                new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction result = service.divide(f1, f2);

        Point[] points = TabulatedFunctionOperationService.asPoints(result);
        for (int i = 0; i < x.length; i++) {
            assertEquals(x[i], points[i].x, 1e-9);
            assertEquals(y1[i] / y2[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testDivideMixedTypes() {
        double[] x = {1, 2, 3};
        double[] y1 = {8, 12, 18};
        double[] y2 = {2, 3, 6};

        TabulatedFunction f1 = new LinkedListTabulatedFunction(x, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x, y2);

        TabulatedFunctionOperationService service =
                new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());

        TabulatedFunction result = service.divide(f1, f2);

        Point[] points = TabulatedFunctionOperationService.asPoints(result);
        for (int i = 0; i < x.length; i++) {
            assertEquals(x[i], points[i].x, 1e-9);
            assertEquals(y1[i] / y2[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testDivideByZeroReturnsInfinity() {
        double[] x = {1, 2, 3};
        double[] y1 = {10, 20, 30};
        double[] y2 = {1, 0, 3}; // деление на ноль во второй точке

        TabulatedFunction f1 = new ArrayTabulatedFunction(x, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x, y2);

        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        // При делении на ноль с double возвращается Infinity
        TabulatedFunction result = service.divide(f1, f2);
        Point[] points = TabulatedFunctionOperationService.asPoints(result);

        assertEquals(10.0, points[0].y, 1e-9);
        assertTrue(Double.isInfinite(points[1].y)); // Должно быть Infinity
        assertEquals(10.0, points[2].y, 1e-9);
    }

    @Test
    void testDivideZeroByZeroReturnsNaN() {
        double[] x = {1, 2, 3};
        double[] y1 = {0, 0, 5};
        double[] y2 = {1, 0, 2}; // деление 0/0 во второй точке

        TabulatedFunction f1 = new ArrayTabulatedFunction(x, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x, y2);

        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunction result = service.divide(f1, f2);
        Point[] points = TabulatedFunctionOperationService.asPoints(result);

        assertEquals(0.0, points[0].y, 1e-9);
        assertTrue(Double.isNaN(points[1].y)); // 0/0 должно быть NaN
        assertEquals(2.5, points[2].y, 1e-9);
    }

    @Test
    void testMultiplyInconsistentCountThrows() {
        double[] x1 = {1, 2, 3};
        double[] y1 = {1, 2, 3};
        double[] x2 = {1, 2};
        double[] y2 = {4, 5};

        TabulatedFunction f1 = new ArrayTabulatedFunction(x1, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x2, y2);

        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        assertThrows(InconsistentFunctionsException.class, () -> service.multiply(f1, f2));
        assertThrows(InconsistentFunctionsException.class, () -> service.divide(f1, f2));
    }

    @Test
    void testDivideInconsistentXThrows() {
        double[] x1 = {1, 2, 3};
        double[] y1 = {1, 2, 3};
        double[] x2 = {1, 2.1, 3}; // разные x
        double[] y2 = {4, 5, 6};

        TabulatedFunction f1 = new ArrayTabulatedFunction(x1, y1);
        TabulatedFunction f2 = new ArrayTabulatedFunction(x2, y2);

        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        assertThrows(InconsistentFunctionsException.class, () -> service.multiply(f1, f2));
        assertThrows(InconsistentFunctionsException.class, () -> service.divide(f1, f2));
    }

    @Test
    void testMultiplicationAndDivisionWithDifferentFactories() {
        double[] x = {1, 2, 3};
        double[] y1 = {2, 4, 6};
        double[] y2 = {1, 2, 3};

        TabulatedFunction f1 = new ArrayTabulatedFunction(x, y1);
        TabulatedFunction f2 = new LinkedListTabulatedFunction(x, y2);

        // Тестируем с ArrayTabulatedFunctionFactory
        TabulatedFunctionOperationService arrayService =
                new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());

        TabulatedFunction multiplyResult = arrayService.multiply(f1, f2);
        TabulatedFunction divideResult = arrayService.divide(f1, f2);

        Point[] multiplyPoints = TabulatedFunctionOperationService.asPoints(multiplyResult);
        Point[] dividePoints = TabulatedFunctionOperationService.asPoints(divideResult);

        for (int i = 0; i < x.length; i++) {
            assertEquals(y1[i] * y2[i], multiplyPoints[i].y, 1e-9);
            assertEquals(y1[i] / y2[i], dividePoints[i].y, 1e-9);
        }

        // Тестируем с LinkedListTabulatedFunctionFactory
        TabulatedFunctionOperationService linkedListService =
                new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction multiplyResult2 = linkedListService.multiply(f1, f2);
        TabulatedFunction divideResult2 = linkedListService.divide(f1, f2);

        Point[] multiplyPoints2 = TabulatedFunctionOperationService.asPoints(multiplyResult2);
        Point[] dividePoints2 = TabulatedFunctionOperationService.asPoints(divideResult2);

        for (int i = 0; i < x.length; i++) {
            assertEquals(y1[i] * y2[i], multiplyPoints2[i].y, 1e-9);
            assertEquals(y1[i] / y2[i], dividePoints2[i].y, 1e-9);
        }
    }
}