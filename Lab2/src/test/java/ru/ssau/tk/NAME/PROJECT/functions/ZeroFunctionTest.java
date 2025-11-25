package ru.ssau.tk.NAME.PROJECT.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.functions.MathFunction;
import ru.ssau.tk.NAME.PROJECT.functions.ZeroFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZeroFunctionTest {
    @Test
    void test() {
        MathFunction f = new ZeroFunction();

        assertEquals(0.0, f.apply(-100.0), 1e-12);
        assertEquals(0.0, f.apply(0.0), 1e-12);
        assertEquals(0.0, f.apply(783.426), 1e-12);
    }
}