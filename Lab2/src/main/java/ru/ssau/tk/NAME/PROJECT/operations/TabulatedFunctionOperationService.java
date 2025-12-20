package ru.ssau.tk.NAME.PROJECT.operations;

import ru.ssau.tk.NAME.PROJECT.exceptions.InconsistentFunctionsException;
import ru.ssau.tk.NAME.PROJECT.functions.Point;
import ru.ssau.tk.NAME.PROJECT.functions.TabulatedFunction;
import ru.ssau.tk.NAME.PROJECT.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.NAME.PROJECT.functions.factory.TabulatedFunctionFactory;

public class TabulatedFunctionOperationService {

    TabulatedFunctionFactory factory;

    private interface BiOperation {
        double apply(double u, double v);
    }

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunctionOperationService() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        Point[] points = new Point[tabulatedFunction.getCount()];

        int i = 0;
        for (Point point : tabulatedFunction) {
            points[i++] = new Point(point.x, point.y);
        }

        return points;
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {
        if (a.getCount() != b.getCount()) throw new InconsistentFunctionsException();

        Point[] pointsF = asPoints(a);
        Point[] pointsG = asPoints(b);

        double[] xValues = new double[a.getCount()];
        double[] yValues = new double[a.getCount()];

        for (int i = 0; i < a.getCount(); ++i) {
            if (Math.abs(pointsF[i].x - pointsG[i].x) > 1e-9) throw new InconsistentFunctionsException();
            xValues[i] = pointsF[i].x;
            yValues[i] = operation.apply(pointsF[i].y, pointsG[i].y);
        }

        return factory.create(xValues, yValues);
    }

    public TabulatedFunction sum(TabulatedFunction F, TabulatedFunction G) {
        return doOperation(F, G, new BiOperation() {
            @Override
            public double apply(double u, double v) { return u + v; }
        });
    }

    public TabulatedFunction subtract(TabulatedFunction F, TabulatedFunction G) {
        return doOperation(F, G, new BiOperation() {
            @Override
            public double apply (double u, double v) { return u - v; }
        });
    }

    public TabulatedFunction multiply(TabulatedFunction F, TabulatedFunction G){
        return doOperation(F, G, new BiOperation() {
            @Override
            public double apply (double u, double v) { return u * v; }
        });
    }

    public TabulatedFunction divide(TabulatedFunction F, TabulatedFunction G){
        return doOperation(F, G, new BiOperation() {
            @Override
            public double apply (double u, double v) { return u / v; }
        });
    }
}