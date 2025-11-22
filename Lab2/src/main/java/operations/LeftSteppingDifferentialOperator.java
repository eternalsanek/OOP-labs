package operations;

import functions.MathFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator {
    public LeftSteppingDifferentialOperator(double step) {
        super(step);
        log.info("Создан левый разностный оператор с шагом {}", step);
    }

    @Override
    public MathFunction derive(MathFunction f) {
        log.debug("Вычисление левой производной для функции {} с шагом {}",
                f.getClass().getSimpleName(), step);
        return new MathFunction() {
            @Override
            public double apply(double x) {
                return (f.apply(x) - f.apply(x - step)) / step;
            }
        };
    }
}
