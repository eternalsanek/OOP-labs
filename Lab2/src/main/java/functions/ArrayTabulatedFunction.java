package functions;

import java.util.Arrays;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Insertable {

    private double[] xValues;
    private double[] yValues;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        this.xValues = Arrays.copyOf(xValues, xValues.length);
        this.yValues = Arrays.copyOf(yValues, yValues.length);
        this.count = xValues.length;
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
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

        if (count > 1) {
            double step = (xEnd - xStart) / (count - 1);

            for (int i = 0; i < count; ++i) {
                this.xValues[i] = xStart + i * step;
                this.yValues[i] = source.apply(xValues[i]);
            }
        }
        else {
            this.xValues[0] = xStart;
            this.yValues[0] = source.apply(xValues[0]);
        }
    }

    public int getCount() {
        return count;
    }

    public double getX(int index) {
        return xValues[index];
    }

    public double getY(int index) {
        return yValues[index];
    }

    public void setY(int index, double value) {
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
        if (x < xValues[0]) {
            return 0;
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
        if (count == 1) return yValues[0];
        return interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
    }

    public double extrapolateRight(double x) {
        if (count == 1) return yValues[0];
        return interpolate(x, xValues[count - 2], xValues[count - 1], yValues[count - 2], yValues[count - 1]);
    }

    public double interpolate(double x, int floorIndex) {
        if (count == 1) return yValues[0];
        return interpolate(x, xValues[floorIndex], xValues[floorIndex + 1],
                yValues[floorIndex], yValues[floorIndex + 1]);
    }
    @Override
    public void insert(double x, double y){
        int index = indexOfX(x);
        if (index != -1){
            yValues[index] = y;
            return;
        }
        if (count == xValues.length){
            int newSize = count + (count / 2) + 1;
            double[] newXValues = new double[newSize];
            double[] newYValues = new double[newSize];
            System.arraycopy(xValues, 0, newXValues, 0, count);
            System.arraycopy(yValues, 0, newYValues, 0, count);
            xValues = newXValues;
            yValues = newYValues;
        }
        int insertIndex = 0;
        while (insertIndex < count && xValues[insertIndex] < x) {
            insertIndex++;
        }
        if (insertIndex < count){
            System.arraycopy(xValues, insertIndex, xValues, insertIndex + 1, count - insertIndex);
            System.arraycopy(yValues, insertIndex, yValues, insertIndex + 1, count - insertIndex);
        }
        xValues[insertIndex] = x;
        yValues[insertIndex] = y;
        count++;
    }
}
