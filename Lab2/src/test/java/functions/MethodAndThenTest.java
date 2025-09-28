package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodAndThenTest {
    @Test
    void test() {
        MathFunction f = new SqrFunction();
        MathFunction g = new IdentityFunction();
        MathFunction h = new ConstantFunction(12.0);
        MathFunction c = new UnitFunction();

        MathFunction fun1 = f.andThen(g).andThen(h);
        assertEquals(12.0, fun1.apply(2.0),  1e-12);
        assertEquals(12.0, fun1.apply(-3.5),  1e-12);

        MathFunction fun2 = g.andThen(f);
        assertEquals(4.0, fun2.apply(2.0),  1e-12);
        assertEquals(9.0, fun2.apply(-3.0),  1e-12);

        MathFunction fun3 = f.andThen(c);
        assertEquals(1.0, fun3.apply(5.0),  1e-12);
        assertEquals(1.0, fun3.apply(-2.0),  1e-12);

        MathFunction fun4 = f.andThen(f);
        assertEquals(16.0, fun4.apply(2.0),  1e-12);
        assertEquals(81.0, fun4.apply(-3.0),  1e-12);
    }
}
