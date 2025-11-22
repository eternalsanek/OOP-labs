package operations;

import functions.MathFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RightSteppingDifferentialOperator extends SteppingDifferentialOperator{
    public RightSteppingDifferentialOperator(double step) {
        super(step);
        log.info("Создан правый разностный оператор с шагом {}", step);
    }

    @Override
    public MathFunction derive(MathFunction f) {
        log.debug("Вычисление правой производной для функции {} с шагом {}",
                f.getClass().getSimpleName(), step);
        return new MathFunction() {
            @Override
            public double apply(double x) {
                return (f.apply(x + step) - f.apply(x)) / step;
            }
        };
    }
}
