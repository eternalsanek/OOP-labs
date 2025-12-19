package ru.ssau.tk.NAME.PROJECT.operations;

import ru.ssau.tk.NAME.PROJECT.functions.MathFunction;

public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator {
    public LeftSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction f) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                return (f.apply(x) - f.apply(x - step)) / step;
            }
        };
    }
}
