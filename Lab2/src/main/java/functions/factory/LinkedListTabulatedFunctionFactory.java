package functions.factory;

import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory{
    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        log.debug("Создание LinkedListTabulatedFunction через фабрику, размер: {}", xValues.length);
        log.info("LinkedListTabulatedFunction создан через фабрику, {} точек", xValues.length);
        return new LinkedListTabulatedFunction(xValues, yValues);
    }
}
