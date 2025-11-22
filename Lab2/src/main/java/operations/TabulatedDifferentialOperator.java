package operations;

import concurrent.SynchronizedTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import static operations.TabulatedFunctionOperationService.asPoints;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {
    private TabulatedFunctionFactory factory;
    @Override
    public TabulatedFunction derive(TabulatedFunction function){
        log.info("Вычисление производной табулированной функции с {} точками", function.getCount());
        Point[] points = asPoints(function);
        int count = points.length;
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        for(int i=0; i < count - 1; i++) {
            xValues[i] = points[i].x;
            double dx = (points[i + 1].x - points[i].x);
            if (Math.abs(dx) < 1e-12) {
                log.error("Деление на ноль при вычислении производной в точке {}: dx = {}", points[i].x, dx);
                throw new ArithmeticException();
            }
            yValues[i] = (points[i + 1].y - points[i].y) / dx;
        }
        xValues[count-1] = points[count - 1].x;
        yValues[count - 1] = yValues[count-2];
        log.info("Производная успешно вычислена, создана новая функция с {} точками", count);
        return factory.create(xValues, yValues);
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        log.debug("Синхронное вычисление производной для функции {}", function.getClass().getSimpleName());
        SynchronizedTabulatedFunction syncFunc;

        if (function instanceof SynchronizedTabulatedFunction) {
            syncFunc = (SynchronizedTabulatedFunction) function;
        } else {
            syncFunc = new SynchronizedTabulatedFunction(function);
        }
        log.debug("Синхронное вычисление производной завершено");
        return syncFunc.doSynchronously(f -> derive(f));
    }

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory){
        this.factory = factory;
        log.info("Создан TabulatedDifferentialOperator с фабрикой {}",
                factory.getClass().getSimpleName());
    }
    public TabulatedDifferentialOperator(){
        this.factory = new ArrayTabulatedFunctionFactory();
        log.info("Создан TabulatedDifferentialOperator с фабрикой по умолчанию");
    }
    public TabulatedFunctionFactory getFactory() {
        return factory;
    }
    public void setFactory(TabulatedFunctionFactory factory){
        log.debug("Установка новой фабрики функций: {}", factory.getClass().getSimpleName());
        this.factory = factory;
    }
}
