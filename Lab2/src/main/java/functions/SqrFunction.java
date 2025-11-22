package functions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqrFunction implements MathFunction{

    @Override
    public double apply(double x) {
        return Math.pow(x, 2);
    }
}
