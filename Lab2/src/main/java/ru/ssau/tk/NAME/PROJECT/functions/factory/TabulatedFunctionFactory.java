package ru.ssau.tk.NAME.PROJECT.functions.factory;

import ru.ssau.tk.NAME.PROJECT.functions.TabulatedFunction;

public interface TabulatedFunctionFactory {
    TabulatedFunction create(double[] xValues, double[] yValues);
}
