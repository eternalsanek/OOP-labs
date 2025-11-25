package ru.ssau.tk.NAME.PROJECT.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.functions.SqrFunction;

import static org.junit.jupiter.api.Assertions.*;

class SqrFunctionTest {

    @Test
    void testApplyWithZero() {
        SqrFunction func = new SqrFunction();
        assertEquals(0.0, func.apply(0.0), 1e-10);
    }

    @Test
    void testApplyWithPositiveIntegers() {
        SqrFunction func = new SqrFunction();
        assertEquals(1.0, func.apply(1.0), 1e-10);
        assertEquals(4.0, func.apply(2.0), 1e-10);
        assertEquals(9.0, func.apply(3.0), 1e-10);
        assertEquals(16.0, func.apply(4.0), 1e-10);
        assertEquals(25.0, func.apply(5.0), 1e-10);
    }

    @Test
    void testApplyWithNegativeIntegers() {
        SqrFunction func = new SqrFunction();
        assertEquals(1.0, func.apply(-1.0), 1e-10);
        assertEquals(4.0, func.apply(-2.0), 1e-10);
        assertEquals(9.0, func.apply(-3.0), 1e-10);
        assertEquals(16.0, func.apply(-4.0), 1e-10);
    }

    @Test
    void testApplyWithFractionalValues() {
        SqrFunction func = new SqrFunction();
        assertEquals(0.25, func.apply(0.5), 1e-10);
        assertEquals(0.25, func.apply(-0.5), 1e-10);
        assertEquals(2.25, func.apply(1.5), 1e-10);
        assertEquals(6.25, func.apply(2.5), 1e-10);
    }

    @Test
    void testApplyWithLargeValues() {
        SqrFunction func = new SqrFunction();
        assertEquals(10000.0, func.apply(100.0), 1e-10);
        assertEquals(10000.0, func.apply(-100.0), 1e-10);
        assertEquals(1.0e10, func.apply(100000.0), 1e-10);
    }

    @Test
    void testApplyWithSmallValues() {
        SqrFunction func = new SqrFunction();
        assertEquals(0.01, func.apply(0.1), 1e-10);
        assertEquals(0.0001, func.apply(0.01), 1e-10);
        assertEquals(1e-10, func.apply(1e-5), 1e-15);
    }
}