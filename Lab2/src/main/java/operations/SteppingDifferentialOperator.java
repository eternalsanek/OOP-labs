package operations;

import functions.MathFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SteppingDifferentialOperator implements DifferentialOperator<MathFunction>{
    protected double step;

    protected SteppingDifferentialOperator (double step) {
        if (step <= 0 || Double.isInfinite(step) || Double.isNaN(step)) {
            log.error("Попытка создания оператора с недопустимым шагом: {}", step);
            throw new IllegalArgumentException();
        }
        this.step = step;
        log.debug("Создан {} с шагом {}", this.getClass().getSimpleName(), step);
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        if (step <= 0 || Double.isInfinite(step) || Double.isNaN(step)) {
            log.error("Попытка установки недопустимого шага: {}", step);
            throw new IllegalArgumentException();
        }
        double oldStep = this.step;
        this.step = step;
        log.debug("Шаг дифференцирования изменен: {} -> {}", oldStep, step);
    }
}
