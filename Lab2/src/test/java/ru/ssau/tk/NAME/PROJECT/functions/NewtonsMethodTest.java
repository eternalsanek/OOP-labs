package ru.ssau.tk.NAME.PROJECT.functions;

import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.functions.FunctionValue;
import ru.ssau.tk.NAME.PROJECT.functions.MathFunction;
import ru.ssau.tk.NAME.PROJECT.functions.NewtonsMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NewtonsMethodTest {
    // Метод находит корень sqrt(2)
    @Test
    public void test1() {
        FunctionValue f = x -> x*x - 2;     // f(x) = x² - 2
        FunctionValue df = x -> 2*x;        // f'(x) = 2x
        MathFunction newton = new NewtonsMethod(f, df);

        double root = newton.apply(1.0);    // начальное приближение = 1
        assertEquals(Math.sqrt(2), root, 1e-6);
    }

    // Метод находит корень 5
    @Test
    public void test2() {
        FunctionValue f = x -> x*x - 25;     // f(x) = x² - 25
        FunctionValue df = x -> 2*x;        // f'(x) = 2x
        MathFunction newton = new NewtonsMethod(f, df);

        double root = newton.apply(100);    // старт из точки 100
        assertEquals(5.0, root, 1e-6);
    }

    // Метод находит корень синуса: f(x) = sin(x)
    @Test
    public void test3() {
        FunctionValue f = Math::sin;       // f(x) = sin(x)
        FunctionValue df = Math::cos;      // f'(x) = cos(x)
        MathFunction newton = new NewtonsMethod(f, df);

        double root = newton.apply(3.0);   // старт из точки 3.0 (рядом с π)
        assertEquals(Math.PI, root, 1e-6);
    }

    // Нет действительных корней: f(x) = x² + 1
    @Test
    public void test4() {
        FunctionValue f = x -> x*x + 1;     // f(x) = x² + 1
        FunctionValue df = x -> 2*x;        // f'(x) = 2x
        MathFunction newton = new NewtonsMethod(f, df);

        assertThrows(ArithmeticException.class, () -> newton.apply(1.0));
    }

    // Производная равна 0: f(x) = x³ + 1, f'(x) = 3x², старт в x=0
    @Test
    public void test5() {
        FunctionValue f = x -> x*x*x + 1;       // f(x) = x³ + 1
        FunctionValue df = x -> 3*x*x;      // f'(x) = 3x²
        MathFunction newton = new NewtonsMethod(f, df);

        assertThrows(ArithmeticException.class, () -> newton.apply(0.0));
    }
}
