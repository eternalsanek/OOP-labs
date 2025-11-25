package ru.ssau.tk.NAME.PROJECT.functions;

public class NewtonsMethod implements MathFunction {

    private FunctionValue function;
    private FunctionValue derivative;

    public NewtonsMethod(FunctionValue function, FunctionValue derivative) {
        this.function = function;
        this.derivative = derivative;
    }

    public double findRoot(double X1) {
        double Xn = X1;
        int count = 0;
        double fx = function.apply(Xn);
        double dfx = derivative.apply(Xn);

        while ((count < 100) && Math.abs(fx) > 1e-6) {
            if (Math.abs(dfx) < 1e-12) {
                throw new ArithmeticException("Derivative is zero, cannot continue Newton's method");
            }

            Xn = Xn - fx / dfx;

            ++count;
            fx = function.apply(Xn);
            dfx = derivative.apply(Xn);
        }

        if (count >= 100 && Math.abs(fx) > 1e-6) {
            throw new ArithmeticException("Newton's method did not converge within 100 iterations");
        }

        return Xn;
    }

    @Override
    public double apply (double x) {
        return findRoot(x);
    }
}
