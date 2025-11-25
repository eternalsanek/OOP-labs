package ru.ssau.tk.NAME.PROJECT.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.functions.*;

import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {

    @Test
    void testApplyWithIdentityAndSqr() {
        MathFunction identity = new IdentityFunction();
        MathFunction sqr = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(identity, sqr);

        assertEquals(0.0, composite.apply(0.0), 1e-10);
        assertEquals(1.0, composite.apply(1.0), 1e-10);
        assertEquals(4.0, composite.apply(2.0), 1e-10);
        assertEquals(9.0, composite.apply(3.0), 1e-10);
        assertEquals(25.0, composite.apply(5.0), 1e-10);
    }

    @Test
    void testApplyWithSqrAndIdentity() {
        MathFunction identity = new IdentityFunction();
        MathFunction sqr = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(sqr, identity);

        assertEquals(0.0, composite.apply(0.0), 1e-10);
        assertEquals(1.0, composite.apply(1.0), 1e-10);
        assertEquals(4.0, composite.apply(2.0), 1e-10);
    }

    @Test
    void testApplyWithConstantAndSqr() {
        MathFunction constant = new ConstantFunction(5.0);
        MathFunction sqr = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(constant, sqr);
        assertEquals(25.0, composite.apply(0.0), 1e-10);
        assertEquals(25.0, composite.apply(10.0), 1e-10);
        assertEquals(25.0, composite.apply(-100.0), 1e-10);
    }

    @Test
    void testApplyWithSqrAndConstant() {
        MathFunction sqr = new SqrFunction();
        MathFunction constant = new ConstantFunction(3.0);
        CompositeFunction composite = new CompositeFunction(sqr, constant);
        assertEquals(3.0, composite.apply(0.0), 1e-10);
        assertEquals(3.0, composite.apply(5.0), 1e-10);
        assertEquals(3.0, composite.apply(-2.0), 1e-10);
    }

    @Test
    void testApplyCompositeOfComposites() {
        MathFunction identity = new IdentityFunction();
        MathFunction sqr = new SqrFunction();
        CompositeFunction inner = new CompositeFunction(identity, sqr);
        CompositeFunction outer = new CompositeFunction(sqr, identity);
        CompositeFunction composite = new CompositeFunction(inner, outer);
        assertEquals(0.0, composite.apply(0.0), 1e-10);
        assertEquals(1.0, composite.apply(1.0), 1e-10);
        assertEquals(16.0, composite.apply(2.0), 1e-10);
        assertEquals(81.0, composite.apply(3.0), 1e-10);
    }

    @Test
    void testApplyWithZeroAndUnitFunctions() {
        MathFunction zero = new ZeroFunction();
        MathFunction unit = new UnitFunction();
        CompositeFunction composite = new CompositeFunction(zero, unit);
        assertEquals(1.0, composite.apply(0.0), 1e-10);
        assertEquals(1.0, composite.apply(100.0), 1e-10);
    }

    @Test
    void testConstructorAndFields() {
        MathFunction f1 = new IdentityFunction();
        MathFunction f2 = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(f1, f2);
        assertEquals(4.0, composite.apply(2.0), 1e-10);
    }

    @Test
    void testApplyWithNegativeValues() {
        MathFunction identity = new IdentityFunction();
        MathFunction sqr = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(identity, sqr);
        assertEquals(4.0, composite.apply(-2.0), 1e-10);
        assertEquals(9.0, composite.apply(-3.0), 1e-10);
        assertEquals(0.25, composite.apply(-0.5), 1e-10);
    }
}