package functions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ZeroFunction extends ConstantFunction {

    public ZeroFunction() {
        super(0.0);
        log.debug("Создана нулевая функция");
    }
}
