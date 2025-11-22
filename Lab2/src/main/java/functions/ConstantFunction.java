package functions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConstantFunction implements MathFunction {

    private final double value;

    public ConstantFunction(double value) {
        this.value = value;
        log.debug("Создана константная функция со значением: {}", value);
    }

    @Override
    public double apply(double x) {
        return value;
    }
}
