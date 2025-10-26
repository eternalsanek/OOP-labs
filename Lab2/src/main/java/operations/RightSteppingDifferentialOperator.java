package operations;

import functions.MathFunction;

public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator{
    public RightSteppingDifferentialOperator(double step) {
        super(step);
    }

    @Override
    public MathFunction derive(MathFunction f) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                return (f.apply(x + step) - f.apply(x)) / step;
            }
        };
    }
}
