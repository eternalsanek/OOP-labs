package ru.ssau.tk.NAME.PROJECT.functions.factory;

import ru.ssau.tk.NAME.PROJECT.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.NAME.PROJECT.functions.TabulatedFunction;

public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory{
    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        return new LinkedListTabulatedFunction(xValues, yValues);
    }
}
