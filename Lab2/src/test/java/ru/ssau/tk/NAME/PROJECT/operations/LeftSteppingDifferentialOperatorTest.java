package ru.ssau.tk.NAME.PROJECT.operations;

import ru.ssau.tk.NAME.PROJECT.functions.MathFunction;
import ru.ssau.tk.NAME.PROJECT.functions.SqrFunction;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.operations.LeftSteppingDifferentialOperator;

import static org.junit.jupiter.api.Assertions.*;

class LeftSteppingDifferentialOperatorTest {
    @Test
    public void testInvalidStepThrows() {
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(0));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(-0.5));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> new LeftSteppingDifferentialOperator(Double.NaN));
    }

    @Test
    public void testLeftDerivativeOfSqrFunction() {
        MathFunction f = new SqrFunction();
        double h = 1e-5;
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(h);
        MathFunction derivative = operator.derive(f);

        double x = 3.0;
        double expected = 2 * x;
        double actual = derivative.apply(x);

        assertEquals(expected, actual, 1e-4);
    }

    @Test
    public void testLeftDerivativeOfLinearFunction() {
        MathFunction f = x -> 5 * x + 7; // f'(x) = 5
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(1e-6);
        MathFunction derivative = operator.derive(f);

        double actual = derivative.apply(10.0);
        assertEquals(5.0, actual, 1e-8);
    }
}