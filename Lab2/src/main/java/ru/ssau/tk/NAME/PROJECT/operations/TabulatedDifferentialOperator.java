package ru.ssau.tk.NAME.PROJECT.operations;

import ru.ssau.tk.NAME.PROJECT.concurrent.SynchronizedTabulatedFunction;
import ru.ssau.tk.NAME.PROJECT.functions.Point;
import ru.ssau.tk.NAME.PROJECT.functions.TabulatedFunction;
import ru.ssau.tk.NAME.PROJECT.functions.factory.ArrayTabulatedFunctionFactory;
import ru.ssau.tk.NAME.PROJECT.functions.factory.TabulatedFunctionFactory;
import static ru.ssau.tk.NAME.PROJECT.operations.TabulatedFunctionOperationService.asPoints;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {
    private TabulatedFunctionFactory factory;
    @Override
    public TabulatedFunction derive(TabulatedFunction function){
        Point[] points = asPoints(function);
        int count = points.length;;
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        for(int i=0; i < count - 1; i++) {
            xValues[i] = points[i].x;
            double dx = (points[i + 1].x - points[i].x);
            if (Math.abs(dx) < 1e-12) {
                throw new ArithmeticException("Division by zero in derivative calculation");
            }
            yValues[i] = (points[i + 1].y - points[i].y) / dx;
        }
        xValues[count-1] = points[count - 1].x;
        yValues[count - 1] = yValues[count-2];
        return factory.create(xValues, yValues);
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        SynchronizedTabulatedFunction syncFunc;

        if (function instanceof SynchronizedTabulatedFunction) {
            syncFunc = (SynchronizedTabulatedFunction) function;
        } else {
            syncFunc = new SynchronizedTabulatedFunction(function);
        }

        return syncFunc.doSynchronously(f -> derive(f));
    }

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory){
        this.factory = factory;
    }
    public TabulatedDifferentialOperator(){
        this.factory = new ArrayTabulatedFunctionFactory();
    }
    public TabulatedFunctionFactory getFactory() {
        return factory;
    }
    public void setFactory(TabulatedFunctionFactory factory){
        this.factory = factory;
    }
}
