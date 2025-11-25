package ru.ssau.tk.NAME.PROJECT.functions.factory;

import ru.ssau.tk.NAME.PROJECT.functions.ArrayTabulatedFunction;
import ru.ssau.tk.NAME.PROJECT.functions.TabulatedFunction;

public class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        return new ArrayTabulatedFunction(xValues, yValues);
    }
}
