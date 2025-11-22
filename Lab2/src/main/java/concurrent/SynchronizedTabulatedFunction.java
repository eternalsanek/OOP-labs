package concurrent;

import functions.Point;
import functions.TabulatedFunction;
import operations.TabulatedFunctionOperationService;
import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction function;

    public SynchronizedTabulatedFunction(TabulatedFunction function) {
        this.function = function;
        log.debug("Создан SynchronizedTabulatedFunction для {}", function.getClass().getSimpleName());
    }

    public interface Operation<T> {
        T apply(SynchronizedTabulatedFunction synchronizedFunction);
    }

    public <T> T doSynchronously(Operation<? extends T> operation) {
        synchronized (function) {
            return operation.apply(this);
        }
    }

    @Override
    public synchronized int getCount() {
        return function.getCount();
    }

    @Override
    public synchronized double getX(int index) {
        return function.getX(index);
    }

    @Override
    public synchronized double getY(int index) {
        return function.getY(index);
    }

    @Override
    public synchronized void setY(int index, double value) {
        function.setY(index, value);
    }

    @Override
    public synchronized int indexOfX(double x) {
        return function.indexOfX(x);
    }

    @Override
    public synchronized int indexOfY(double y) {
        return function.indexOfY(y);
    }

    @Override
    public synchronized double leftBound() {
        return function.leftBound();
    }

    @Override
    public synchronized double rightBound() {
        return function.rightBound();
    }

    @Override
    public synchronized double apply(double x) {
        return function.apply(x);
    }

    @Override
    public synchronized Iterator<Point> iterator() {
        synchronized (function){
            Point[] copyfunction = TabulatedFunctionOperationService.asPoints(function);
            log.debug("Создан массив из {} точек для итератора", copyfunction.length);
            return new Iterator<Point>(){
                private int currentIndex = 0;
                private final Point[] points = copyfunction;
                @Override
                public boolean hasNext(){
                    return currentIndex < points.length;
                }
                @Override
                public Point next(){
                    if(!hasNext()){
                        log.error("Попытка вызова next() когда элементов больше нет");
                        throw new NoSuchElementException();
                    }
                    return points[currentIndex++];
                }
                @Override
                public void remove(){
                    log.error("Попытка вызова remove() у итератора");
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}
