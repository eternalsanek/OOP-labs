package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ZeroFunctionTest {

    @Test
    void testApplyReturnsZero() {
        ZeroFunction func = new ZeroFunction();
        assertEquals(0.0, func.apply(0.0), 1e-10);
        assertEquals(0.0, func.apply(1.0), 1e-10);
        assertEquals(0.0, func.apply(-1.0), 1e-10);
        assertEquals(0.0, func.apply(100.0), 1e-10);
        assertEquals(0.0, func.apply(-100.0), 1e-10);
        assertEquals(0.0, func.apply(123.456), 1e-10);
    }

    @Test
    void testConstructor() {
        ZeroFunction func = new ZeroFunction();
        assertEquals(0.0, func.apply(999.0), 1e-10);
    }

    @Test
    void testInheritance() {
        ZeroFunction zeroFunc = new ZeroFunction();
        assertTrue(zeroFunc instanceof ConstantFunction);
        MathFunction mathFunc = zeroFunc;
        assertEquals(0.0, mathFunc.apply(5.0), 1e-10);
    }
}