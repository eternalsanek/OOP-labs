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
}