package operations;

import exceptions.InconsistentFunctionsException;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;

import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TabulatedFunctionOperationService {

    TabulatedFunctionFactory factory;

    private interface BiOperation {
        double apply(double u, double v);
    }

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        this.factory = factory;
        log.info("Создан TabulatedFunctionOperationService с фабрикой {}", factory.getClass().getSimpleName());
    }

    public TabulatedFunctionOperationService() {
        this.factory = new ArrayTabulatedFunctionFactory();
        log.info("Создан TabulatedFunctionOperationService с фабрикой по умолчанию");
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
        log.debug("Установка новой фабрики функций: {}", factory.getClass().getSimpleName());
    }

    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        log.debug("Преобразование функции в массив точек, количество: {}", tabulatedFunction.getCount());
        Point[] points = new Point[tabulatedFunction.getCount()];

        int i = 0;
        for (Point point : tabulatedFunction) {
            points[i++] = new Point(point.x, point.y);
        }

        return points;
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {
        log.info("Выполнение операции над функциями: {} и {}", a.getClass().getSimpleName(), b.getClass().getSimpleName());
        if (a.getCount() != b.getCount()) {
            log.error("Несовпадение количества точек: {} != {}", a.getCount(), b.getCount());
            throw new InconsistentFunctionsException();
        }

        Point[] pointsF = asPoints(a);
        Point[] pointsG = asPoints(b);

        double[] xValues = new double[a.getCount()];
        double[] yValues = new double[a.getCount()];

        for (int i = 0; i < a.getCount(); ++i) {
            if (Math.abs(pointsF[i].x - pointsG[i].x) > 1e-9) {
                log.error("Несовпадение X-координат в точке {}: {} != {}",
                        i, pointsF[i].x, pointsG[i].x);
                throw new InconsistentFunctionsException();
            }
            xValues[i] = pointsF[i].x;
            yValues[i] = operation.apply(pointsF[i].y, pointsG[i].y);
        }
        log.info("Операция над функциями завершена, создана новая функция с {} точками", a.getCount());
        return factory.create(xValues, yValues);
    }

    public TabulatedFunction sum(TabulatedFunction F, TabulatedFunction G) {
        log.debug("Сложение функций: {} + {}", F.getClass().getSimpleName(), G.getClass().getSimpleName());
        return doOperation(F, G, new BiOperation() {
            @Override
            public double apply(double u, double v) { return u + v; }
        });
    }

    public TabulatedFunction subtract(TabulatedFunction F, TabulatedFunction G) {
        log.debug("Вычитание функций: {} - {}", F.getClass().getSimpleName(), G.getClass().getSimpleName());
        return doOperation(F, G, new BiOperation() {
            @Override
            public double apply (double u, double v) { return u - v; }
        });
    }

    public TabulatedFunction multiply(TabulatedFunction F, TabulatedFunction G){
        log.debug("Умножение функций: {} * {}", F.getClass().getSimpleName(), G.getClass().getSimpleName());
        return doOperation(F, G, new BiOperation() {
            @Override
            public double apply (double u, double v) { return u * v; }
        });
    }

    public TabulatedFunction divide(TabulatedFunction F, TabulatedFunction G){
        log.debug("Деление функций: {} / {}", F.getClass().getSimpleName(), G.getClass().getSimpleName());
        return doOperation(F, G, new BiOperation() {
            @Override
            public double apply (double u, double v) {
                if (Math.abs(v) < 1e-12) {
                    log.error("Деление на ноль: {} / {}", u, v);
                    throw new ArithmeticException();
                }
                return u / v;
            }
        });
    }
}