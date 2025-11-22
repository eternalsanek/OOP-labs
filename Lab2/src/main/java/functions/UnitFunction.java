package functions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UnitFunction extends ConstantFunction {

    public UnitFunction() {
        super(1.0);
        log.debug("Создана единичная функция");
    }
}
