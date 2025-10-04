package functions;

public class RungeKutta4Method implements MathFunction {
    private final OrdinaryDifferentialEquation derivative;
    private double initialValueX;
    private double initialValueY;
    private final double stepSize;
    public RungeKutta4Method(OrdinaryDifferentialEquation derivative, double initialValueX, double initialValueY, double stepSize){
        this.derivative = derivative;
        this.initialValueX = initialValueX;
        this.initialValueY = initialValueY;
        this.stepSize = Math.abs(stepSize);
    }

    @Override
    public double apply(double x){ //конечное значение x, до которого считать
        if (Math.abs(x-initialValueX) < 1e-10){
            return initialValueY;
        }
        double direction = (x > initialValueX) ? 1.0 : -1.0;
        double valueH = stepSize * direction;
        int numberOfSteps = (int) Math.ceil(Math.abs(x - initialValueX) / stepSize);
        double currentValueX = initialValueX;
        double currentValueY = initialValueY;
        for(int i = 0; i < numberOfSteps; ++i) {
            double currentStep = valueH;
            if (i == numberOfSteps - 1){
                currentStep = x - currentValueX;
            }
            double k1 = currentStep * derivative.apply(currentValueX, currentValueY);
            double k2 = currentStep * derivative.apply(currentValueX + currentStep/2, currentValueY + k1/2);
            double k3 = currentStep * derivative.apply(currentValueX + currentStep/2, currentValueY + k2/2);
            double k4 = currentStep * derivative.apply(currentValueX + currentStep, currentValueY + k3);
            currentValueY += ((k1 + 2*k2 + 2*k3 + k4) / 6);
            currentValueX += currentStep;
            if(Math.abs(currentValueX - x) < 1e-10){
                break;
            }
        }
        return currentValueY;
    }
}
