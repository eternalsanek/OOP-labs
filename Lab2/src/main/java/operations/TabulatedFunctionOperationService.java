package operations;

import functions.Point;
import functions.TabulatedFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class TabulatedFunctionOperationService {
    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        Point[] points = new Point[tabulatedFunction.getCount()];

        int i = 0;
        for (Point point : tabulatedFunction) {
            points[i++] = new Point(point.x, point.y);
        }

        return points;
    }
}