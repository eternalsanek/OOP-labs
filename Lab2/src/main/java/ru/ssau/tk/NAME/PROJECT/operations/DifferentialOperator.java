package ru.ssau.tk.NAME.PROJECT.operations;

import ru.ssau.tk.NAME.PROJECT.functions.MathFunction;

public interface DifferentialOperator<T extends MathFunction> {
    T derive(T function);
}
