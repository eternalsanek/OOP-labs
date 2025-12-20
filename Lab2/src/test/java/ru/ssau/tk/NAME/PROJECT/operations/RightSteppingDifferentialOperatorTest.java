package ru.ssau.tk.NAME.PROJECT.operations;

import ru.ssau.tk.NAME.PROJECT.functions.MathFunction;
import ru.ssau.tk.NAME.PROJECT.functions.SqrFunction;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.operations.RightSteppingDifferentialOperator;

import static org.junit.jupiter.api.Assertions.*;

class RightSteppingDifferentialOperatorTest {
    @Test
    public void testInvalidStepThrows() {
        assertThrows(IllegalArgumentException.class, () -> new RightSteppingDifferentialOperator(0));
        assertThrows(IllegalArgumentException.class, () -> new RightSteppingDifferentialOperator(-0.5));
        assertThrows(IllegalArgumentException.class, () -> new RightSteppingDifferentialOperator(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> new RightSteppingDifferentialOperator(Double.NaN));
    }

    @Test
    public void testRightDerivativeOfSqrFunction() {
        MathFunction f = new SqrFunction();
        double h = 1e-5;
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(h);
        MathFunction derivative = operator.derive(f);

        double x = 3.0;
        double expected = 2 * x;
        double actual = derivative.apply(x);

        assertEquals(expected, actual, 1e-4);
    }

    @Test
    public void testRightDerivativeOfLinearFunction() {
        MathFunction f = x -> -2 * x + 1;
        RightSteppingDifferentialOperator operator = new RightSteppingDifferentialOperator(1e-6);
        MathFunction derivative = operator.derive(f);

        double actual = derivative.apply(5.0);
        assertEquals(-2.0, actual, 1e-8);
    }
}