package functions;

public class ODEadapter implements MathFunction {
    private final OrdinaryDifferentialEquation function;
    private final double existY;
    public ODEadapter(OrdinaryDifferentialEquation function, double existY){
        this.function = function;
        this.existY = existY;
    }
    @Override
    public double apply(double x){
        return function.apply(x, existY);
    }
}
