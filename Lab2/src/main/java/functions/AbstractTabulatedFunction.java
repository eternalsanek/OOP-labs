package functions;

import exceptions.DifferentLengthOfArraysException;
import exceptions.ArrayIsNotSortedException;

import java.io.Serializable;

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
        if (x < leftBound()) {
            return extrapolateLeft(x);
        }
        else if (x > rightBound()) {
            return extrapolateRight(x);
        }
        else {
            int index = indexOfX(x);
            if (index != -1) {
                return getY(index);
            }
            else {
                int floorIndex = floorIndexOfX(x);
                return interpolate(x, floorIndex);
            }
        }
    }

    public static void checkLengthIsTheSame(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) throw new DifferentLengthOfArraysException("The lengths " +
                "of the arrays are not the same");
    }

    public static void checkSorted(double[] xValues) {
        for (int i = 0; i < xValues.length - 1; ++i) {
            if (xValues[i] >= xValues[i + 1]) throw new ArrayIsNotSortedException("The array " +
                    "is not sorted in ascending order");
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
