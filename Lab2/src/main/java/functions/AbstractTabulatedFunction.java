package functions;

import exceptions.DifferentLengthOfArraysException;
import exceptions.ArrayIsNotSortedException;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractTabulatedFunction implements TabulatedFunction, Serializable {

    protected int count;

    protected abstract int floorIndexOfX(double x);

    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);

    protected abstract double interpolate(double x, int floorIndex);

    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        return leftY + (rightY - leftY) * (x - leftX) / (rightX - leftX);
    }

    @Override
    public double apply(double x) {
        log.debug("Вычисление функции для x = {}", x);
        if (x < leftBound()) {
            log.debug("x < левой границы, экстраполяция слева");
            return extrapolateLeft(x);
        }
        else if (x > rightBound()) {
            log.debug("x > правой границы, экстраполяция справа");
            return extrapolateRight(x);
        }
        else {
            int index = indexOfX(x);
            if (index != -1) {
                log.debug("x найден в таблице, возвращаем соответствующее y");
                return getY(index);
            }
            else {
                int floorIndex = floorIndexOfX(x);
                log.debug("x не найден, интерполяция с floor индексом {}", floorIndex);
                return interpolate(x, floorIndex);
            }
        }
    }

    public static void checkLengthIsTheSame(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            log.error("Длины массивов X и Y не совпадают: {} != {}", xValues.length, yValues.length);
            throw new DifferentLengthOfArraysException("The lengths " + "of the arrays are not the same");
        }
    }

    public static void checkSorted(double[] xValues) {
        for (int i = 0; i < xValues.length - 1; ++i) {
            if (xValues[i] >= xValues[i + 1]) {
                log.error("Массив X не отсортирован: x[{}] = {} >= x[{}] = {}", i, xValues[i], i + 1, xValues[i + 1]);
                throw new ArrayIsNotSortedException("The array is not sorted in ascending order");
            }
        }
    }
    @Override
    public String toString(){
        String resultString;
        StringBuilder line = new StringBuilder();
        line.append(getClass().getSimpleName()).append(" size = ").append(getCount()).append("\n");
        for(Point point : this){
            line.append("[").append(point.x).append("; ").append(point.y).append("]\n");
        }
        resultString = line.toString();
        return resultString;
    }
}
