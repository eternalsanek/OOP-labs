package functions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NewtonsMethod implements MathFunction {

    private FunctionValue function;
    private FunctionValue derivative;

    public NewtonsMethod(FunctionValue function, FunctionValue derivative) {
        this.function = function;
        this.derivative = derivative;
        log.debug("Создан метод Ньютона для функции {} и производной {}", function.getClass().getSimpleName(), derivative.getClass().getSimpleName());
    }

    public double findRoot(double X1) {
        log.info("Поиск корня методом Ньютона, начальное приближение: {}", X1);
        double Xn = X1;
        int count = 0;
        double fx = function.apply(Xn);
        double dfx = derivative.apply(Xn);
        log.debug("Начальные значения: f({}) = {}, f'({}) = {}", Xn, fx, Xn, dfx);

        while ((count < 100) && Math.abs(fx) > 1e-6) {
            if (Math.abs(dfx) < 1e-12) {
                log.error("Производная близка к нулю: f'({}) = {}", Xn, dfx);
                throw new ArithmeticException("Derivative is zero, cannot continue Newton's method");
            }

            Xn = Xn - fx / dfx;

            ++count;
            fx = function.apply(Xn);
            dfx = derivative.apply(Xn);
        }

        if (count >= 100 && Math.abs(fx) > 1e-6) {
            log.warn("Метод Ньютона не сошелся за {} итераций, последнее значение: f({}) = {}", count, Xn, fx);
            throw new ArithmeticException("Newton's method did not converge within 100 iterations");
        }
        log.info("Корень найден: x = {} за {} итераций, f(x) = {}", Xn, count, fx);
        return Xn;
    }

    @Override
    public double apply (double x) {
        log.debug("Применение метода Ньютона для x = {}", x);
        return findRoot(x);
    }
}
