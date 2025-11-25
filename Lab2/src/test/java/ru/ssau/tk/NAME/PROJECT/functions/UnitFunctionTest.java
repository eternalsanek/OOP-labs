package ru.ssau.tk.NAME.PROJECT.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.functions.MathFunction;
import ru.ssau.tk.NAME.PROJECT.functions.UnitFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnitFunctionTest {
    @Test
    void test() {
        MathFunction f = new UnitFunction();

        assertEquals(1.0, f.apply(-999.0), 1e-12);
        assertEquals(1.0, f.apply(0.4), 1e-12);
        assertEquals(1.0, f.apply(42.89), 1e-12);
    }
}