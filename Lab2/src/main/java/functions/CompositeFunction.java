package functions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompositeFunction implements MathFunction {
    private final MathFunction firstFunction;
    private final MathFunction secondFunction;
    public CompositeFunction(MathFunction firstFunction, MathFunction secondFunction){
        this.firstFunction = firstFunction;
        this.secondFunction = secondFunction;
        log.debug("Создана композитная функция: {} ∘ {}", secondFunction.getClass().getSimpleName(), firstFunction.getClass().getSimpleName());
    }
    @Override
    public double apply(double x) {
        return secondFunction.apply(firstFunction.apply(x));
    }
}
