package ru.ssau.tk.NAME.PROJECT.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.functions.ConstantFunction;
import ru.ssau.tk.NAME.PROJECT.functions.MathFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstantFunctionTest {

    @Test
    void test() {
        MathFunction f = new ConstantFunction(5.0);

        assertEquals(5.0, f.apply(-10.0), 1e-12);
        assertEquals(5.0, f.apply(0.0), 1e-12);
        assertEquals(5.0, f.apply(1000.0), 1e-12);
    }
}