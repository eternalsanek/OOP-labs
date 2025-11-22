package functions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdentityFunction implements MathFunction {
    @Override
    public double apply (double x) {
        return x;
    }
}
