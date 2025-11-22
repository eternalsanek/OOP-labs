package functions;

import exceptions.InterpolationException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, Serializable {
    private static final long serialVersionUID = -2939010482140999734L;

    private double[] xValues;
    private double[] yValues;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        log.debug("Создание ArrayTabulatedFunction из массивов, размер: {}", xValues.length);
        if (xValues.length < 2){
            log.error("Недостаточно точек для создания функции: {}", xValues.length);
            throw new IllegalArgumentException("At least 2 points required");
        }

        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        this.xValues = Arrays.copyOf(xValues, xValues.length);
        this.yValues = Arrays.copyOf(yValues, yValues.length);
        this.count = xValues.length;
        log.info("ArrayTabulatedFunction создан успешно, {} точек, диапазон [{}, {}]", count, xValues[0], xValues[count-1]);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        log.debug("Создание ArrayTabulatedFunction из функции {}, диапазон [{}, {}], {} точек", source.getClass().getSimpleName(), xFrom, xTo, count);
        if (count < 2){
            log.error("Недостаточно точек для создания функции: {}", count);
            throw new IllegalArgumentException("At least 2 points required");
        }
        double xStart, xEnd;
        if (xFrom > xTo) {
            xStart = xTo;
            xEnd = xFrom;
            log.debug("Границы переставлены: [{}, {}] -> [{}, {}]", xFrom, xTo, xStart, xEnd);
        }
        else {
            xStart = xFrom;
            xEnd = xTo;
        }
        this.count = count;
        this.xValues = new double[count];
        this.yValues = new double[count];
        double step = (xEnd - xStart) / (count - 1);
        for (int i = 0; i < count; ++i) {
            this.xValues[i] = xStart + i * step;
            this.yValues[i] = source.apply(xValues[i]);
        }
        log.info("ArrayTabulatedFunction создан из функции, {} точек, диапазон [{}, {}]", count, xStart, xEnd);
    }

    public int getCount() {
        return count;
    }

    public double getX(int index) {
        if (index < 0 || index >= count) {
            log.error("Неверный индекс: {}, допустимый диапазон [0, {}]", index, count-1);
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        return xValues[index];
    }

    public double getY(int index) {
        if (index < 0 || index >= count) {
            log.error("Неверный индекс: {}, допустимый диапазон [0, {}]", index, count-1);
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        return yValues[index];
    }

    public void setY(int index, double value) {
        log.debug("Установка Y[{}] = {}", index, value);
        if (index < 0 || index >= count) {
            log.error("Неверный индекс: {}, допустимый диапазон [0, {}]", index, count-1);
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        yValues[index] = value;
    }

    public int indexOfX(double x) {
        for (int i = 0; i < count; ++i) {
            if (Math.abs(xValues[i] - x) < 1e-12) {
                log.debug("Найден индекс {} для X = {}", i, x);
                return i;
            }
        }
        return -1;
    }

    public int indexOfY(double y) {
        for (int i = 0; i < count; ++i) {
            if (Math.abs(yValues[i] - y) < 1e-12) {
                log.debug("Найден индекс {} для Y = {}", i, y);
                return i;
            }
        }
        return -1;
    }

    public double leftBound() {
        return xValues[0];
    }

    public double rightBound() {
        return xValues[count - 1];
    }

    // Предполагается, что x не равен ни одному элементу из xValues.
    // Такая ситуация обрабатывается в indexOfX.
    public int floorIndexOfX(double x) {
        if (x < leftBound()){
            log.error("X = {} меньше левой границы {}", x, leftBound());
            throw new IllegalArgumentException("The value is less than the left bound");
        }

        if (x > xValues[count - 1]) {
            log.debug("X = {} больше правой границы, возвращаем {}", x, count);
            return count;
        }

        for (int i = 0; i < count - 1; i++) {
            if (xValues[i] < x && x < xValues[i + 1]) {
                log.debug("Найден floor индекс {} для X = {}", i, x);
                return i;
            }
        }

        return -1;
    }

    public double extrapolateLeft(double x) {
        log.debug("Экстраполяция слева для X = {}", x);
        return interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
    }

    public double extrapolateRight(double x) {
        log.debug("Экстраполяция справа для X = {}", x);
        return interpolate(x, xValues[count - 2], xValues[count - 1], yValues[count - 2], yValues[count - 1]);
    }

    public double interpolate(double x, int floorIndex) {
        log.debug("Интерполяция для X = {} с floor индексом {}", x, floorIndex);
        if (x < xValues[floorIndex] || x > xValues[floorIndex + 1]) {
            log.error("X = {} вне интервала интерполяции [{}, {}]", x, xValues[floorIndex], xValues[floorIndex + 1]);
            throw new InterpolationException("X is outside the interpolation interval");
        }

        return interpolate(x, xValues[floorIndex], xValues[floorIndex + 1],
                yValues[floorIndex], yValues[floorIndex + 1]);
    }

    @Override
    public void insert(double x, double y){
        log.debug("Вставка точки: x = {}, y = {}", x, y);
        int insertIndex = 0;
        while (insertIndex < count && xValues[insertIndex] <= x) {
            if (Math.abs(xValues[insertIndex] - x) <= 1e-12){
                log.debug("Найдена существующая точка, обновление Y");
                yValues[insertIndex] = y;
                return;
            }
            if (xValues[insertIndex] < x) {
                insertIndex++;
            }
        }
        log.debug("Вставка новой точки на позицию {}", insertIndex);
        double[] newXValues = new double[count + 1];
        double[] newYValues = new double[count + 1];
        System.arraycopy(xValues, 0, newXValues, 0, insertIndex);
        System.arraycopy(yValues, 0, newYValues, 0, insertIndex);
        newXValues[insertIndex] = x;
        newYValues[insertIndex] = y;
        System.arraycopy(xValues, insertIndex, newXValues, insertIndex + 1, count - insertIndex);
        System.arraycopy(yValues, insertIndex, newYValues, insertIndex + 1, count - insertIndex);
        xValues = newXValues;
        yValues = newYValues;
        count++;
        log.info("Точка вставлена на позицию {}, новый размер: {}", insertIndex, count);
    }

    @Override
    public void remove(int index) {
        log.debug("Удаление точки с индексом {}", index);
        if (index < 0 || index >= count) {
            log.error("Неверный индекс для удаления: {}, размер: {}", index, count);
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        System.arraycopy(xValues, index + 1, xValues, index, count - index - 1);
        System.arraycopy(yValues, index + 1, yValues, index, count - index - 1);
        count--;
        log.info("Точка с индексом {} удалена, новый размер: {}", index, count);
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<Point>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    log.error("Попытка вызова next() когда элементов больше нет");
                    throw new NoSuchElementException();
                }

                Point point = new Point(xValues[i], yValues[i]);
                i++;
                return point;
            }
        };
    }

}
