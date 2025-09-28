package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConstantFunctionTest {

    @Test
    void testApplyWithPositiveConstant() {
        ConstantFunction func = new ConstantFunction(5.0);
        assertEquals(5.0, func.apply(0.0), 1e-10);
        assertEquals(5.0, func.apply(10.0), 1e-10);
        assertEquals(5.0, func.apply(-5.0), 1e-10);
        assertEquals(5.0, func.apply(100.0), 1e-10);
        assertEquals(5.0, func.apply(-100.0), 1e-10);
    }

    @Test
    void testApplyWithZeroConstant() {
        ConstantFunction func = new ConstantFunction(0.0);
        assertEquals(0.0, func.apply(0.0), 1e-10);
        assertEquals(0.0, func.apply(1.0), 1e-10);
        assertEquals(0.0, func.apply(-1.0), 1e-10);
        assertEquals(0.0, func.apply(123.45), 1e-10);
    }

    @Test
    void testApplyWithNegativeConstant() {
        ConstantFunction func = new ConstantFunction(-3.5);
        assertEquals(-3.5, func.apply(0.0), 1e-10);
        assertEquals(-3.5, func.apply(10.0), 1e-10);
        assertEquals(-3.5, func.apply(-10.0), 1e-10);
    }

    @Test
    void testApplyWithFractionalConstant() {
        ConstantFunction func = new ConstantFunction(2.75);
        assertEquals(2.75, func.apply(0.0), 1e-10);
        assertEquals(2.75, func.apply(1.0), 1e-10);
        assertEquals(2.75, func.apply(-1.0), 1e-10);
    }

    @Test
    void testConstructor() {
        ConstantFunction func = new ConstantFunction(7.0);
        assertEquals(7.0, func.apply(999.0), 1e-10);
    }
}