package functions.factory;

import functions.ArrayTabulatedFunction;
import functions.TabulatedFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        log.debug("Создание ArrayTabulatedFunction через фабрику, размер: {}", xValues.length);
        log.info("ArrayTabulatedFunction создан через фабрику, {} точек", xValues.length);
        return new ArrayTabulatedFunction(xValues, yValues);
    }
}
