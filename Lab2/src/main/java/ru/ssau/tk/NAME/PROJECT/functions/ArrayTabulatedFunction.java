package ru.ssau.tk.NAME.PROJECT.functions;

import ru.ssau.tk.NAME.PROJECT.exceptions.InterpolationException;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, Serializable {
    private static final long serialVersionUID = -2939010482140999734L;

    private double[] xValues;
    private double[] yValues;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length < 2){
            throw new IllegalArgumentException("At least 2 points required");
        }

        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);

        this.xValues = Arrays.copyOf(xValues, xValues.length);
        this.yValues = Arrays.copyOf(yValues, yValues.length);
        this.count = xValues.length;
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        if (count < 2){
            throw new IllegalArgumentException("At least 2 points required");
        }
        double xStart, xEnd;
        if (xFrom > xTo) {
            xStart = xTo;
            xEnd = xFrom;
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
    }

    public int getCount() {
        return count;
    }

    public double getX(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        return xValues[index];
    }

    public double getY(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        return yValues[index];
    }

    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        yValues[index] = value;
    }

    public int indexOfX(double x) {
        for (int i = 0; i < count; ++i) {
            if (Math.abs(xValues[i] - x) < 1e-12) return i;
        }
        return -1;
    }

    public int indexOfY(double y) {
        for (int i = 0; i < count; ++i) {
            if (Math.abs(yValues[i] - y) < 1e-12) return i;
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
            throw new IllegalArgumentException("The value is less than the left bound");
        }

        if (x > xValues[count - 1]) {
            return count;
        }

        for (int i = 0; i < count - 1; i++) {
            if (xValues[i] < x && x < xValues[i + 1]) {
                return i;
            }
        }

        return -1;
    }

    public double extrapolateLeft(double x) {
        return interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
    }

    public double extrapolateRight(double x) {
        return interpolate(x, xValues[count - 2], xValues[count - 1], yValues[count - 2], yValues[count - 1]);
    }

    public double interpolate(double x, int floorIndex) {
        if (x < xValues[floorIndex] || x > xValues[floorIndex + 1]) {
            throw new InterpolationException("X is outside the interpolation interval");
        }

        return interpolate(x, xValues[floorIndex], xValues[floorIndex + 1],
                yValues[floorIndex], yValues[floorIndex + 1]);
    }

    @Override
    public void insert(double x, double y){
        int insertIndex = 0;
        boolean found = false;
        int index = indexOfX(x);
        while (insertIndex < count && xValues[insertIndex] <= x) {
            if (Math.abs(xValues[insertIndex] - x) <= 1e-12){
                yValues[insertIndex] = y;
                return;
            }
            if (xValues[insertIndex] < x) {
                insertIndex++;
            }
        }
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
    }

    @Override
    public void remove(int index) {
        if (index < 0 || index >= count) {
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        System.arraycopy(xValues, index + 1, xValues, index, count - index - 1);
        System.arraycopy(yValues, index + 1, yValues, index, count - index - 1);
        count--;
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
                    throw new NoSuchElementException();
                }

                Point point = new Point(xValues[i], yValues[i]);
                i++;
                return point;
            }
        };
    }

}
