package operations;

import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import static operations.TabulatedFunctionOperationService.asPoints;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {
    private TabulatedFunctionFactory factory;
    @Override
    public TabulatedFunction derive(TabulatedFunction function){
        Point[] points = asPoints(function);
        int count = points.length;;
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        for(int i=0; i < count; i++){
            xValues[i] = points[i].x;
            if(i != count - 1){
                double dx = (points[i+1].x - points[i].x);
                if(Math.abs(dx) < 1e-12){
                    throw new ArithmeticException("Division by zero in derivative calculation");
                }
                yValues[i] = (points[i+1].y - points[i].y)/dx;
            }
            else{
                yValues[i] = yValues[i-1];
            }
        }
        return factory.create(xValues, yValues);
    }
    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory){
        this.factory = factory;
    }
    public TabulatedDifferentialOperator(){
        this.factory = new ArrayTabulatedFunctionFactory();
    }
    public TabulatedFunctionFactory getFactory() {
        return factory;
    }
    public void setFactory(TabulatedFunctionFactory factory){
        this.factory = factory;
    }
}
