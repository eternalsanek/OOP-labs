package ru.ssau.tk.NAME.PROJECT.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.functions.*;

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

    // Тестим сложные функции из табулированных и обычных функций:
    @Test
    void test1() {
        double[] x1 = {0.0, 1.0, 2.0};
        double[] y1 = {0.0, 1.0, 4.0};
        ArrayTabulatedFunction f1 = new ArrayTabulatedFunction(x1, y1);

        double[] x2 = {0.0, 1.0, 2.0};
        double[] y2 = {0.0, 2.0, 4.0};
        ArrayTabulatedFunction f2 = new ArrayTabulatedFunction(x2, y2);

        MathFunction composed = f1.andThen(f2);

        assertEquals(0.0, composed.apply(0.0), 1e-12);
        assertEquals(2.0, composed.apply(1.0), 1e-12);
        assertEquals(8.0, composed.apply(2.0), 1e-12);
        assertEquals(5.0, composed.apply(1.5), 1e-12); // проверка экстраполяции
    }

    @Test
    void test2() {
        double[] x1 = {0.0, 1.0, 2.0};
        double[] y1 = {1.0, 2.0, 3.0};
        ArrayTabulatedFunction f1 = new ArrayTabulatedFunction(x1, y1);

        double[] x2 = {0.0, 2.0, 3.0};
        double[] y2 = {0.0, 4.0, 9.0};
        LinkedListTabulatedFunction f2 = new LinkedListTabulatedFunction(x2, y2);

        MathFunction composed = f1.andThen(f2);

        assertEquals(2.0, composed.apply(0.0), 1e-12);
        assertEquals(4.0, composed.apply(1.0), 1e-12);
        assertEquals(9.0, composed.apply(2.0), 1e-12);
        assertEquals(3.0, composed.apply(0.5), 1e-12); // интерполяция
    }

    @Test
    void test3() {
        double[] x1 = {0.0, 1.0, 2.0};
        double[] y1 = {0.0, 1.0, 2.0};
        ArrayTabulatedFunction f1 = new ArrayTabulatedFunction(x1, y1);

        // g(x) = x^2
        MathFunction g = new SqrFunction();

        MathFunction composed = f1.andThen(g);

        assertEquals(0.0, composed.apply(0.0), 1e-12);
        assertEquals(1.0, composed.apply(1.0), 1e-12);
        assertEquals(4.0, composed.apply(2.0), 1e-12);
        assertEquals(0.25, composed.apply(0.5), 1e-12); // интерполяция
    }

}
