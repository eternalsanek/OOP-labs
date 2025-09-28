package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnitFunctionTest {

    @Test
    void testApplyReturnsOne() {
        UnitFunction func = new UnitFunction();
        assertEquals(1.0, func.apply(0.0), 1e-10);
        assertEquals(1.0, func.apply(1.0), 1e-10);
        assertEquals(1.0, func.apply(-1.0), 1e-10);
        assertEquals(1.0, func.apply(100.0), 1e-10);
        assertEquals(1.0, func.apply(-100.0), 1e-10);
        assertEquals(1.0, func.apply(123.456), 1e-10);
    }

    @Test
    void testConstructor() {
        UnitFunction func = new UnitFunction();
        assertEquals(1.0, func.apply(999.0), 1e-10);
    }

    @Test
    void testInheritance() {
        UnitFunction unitFunc = new UnitFunction();
        assertTrue(unitFunc instanceof ConstantFunction);
        MathFunction mathFunc = unitFunc;
        assertEquals(1.0, mathFunc.apply(5.0), 1e-10);
    }
}