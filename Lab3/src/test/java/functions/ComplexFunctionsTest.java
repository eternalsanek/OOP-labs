package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ComplexFunctionsTest {

    @Test
    void testIdentityFunctionWithTabulatedArray() {
        MathFunction identity = new IdentityFunction();
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);

        CompositeFunction comp1 = new CompositeFunction(identity, arrayFunc);
        assertEquals(10.0, comp1.apply(1.0), 1e-10);
        assertEquals(20.0, comp1.apply(2.0), 1e-10);

        CompositeFunction comp2 = new CompositeFunction(arrayFunc, identity);
        assertEquals(10.0, comp2.apply(1.0), 1e-10);
        assertEquals(20.0, comp2.apply(2.0), 1e-10);
    }

    @Test
    void testIdentityFunctionWithTabulatedLinkedList() {
        MathFunction identity = new IdentityFunction();
        double[] xValues = {0.5, 1.5, 2.5};
        double[] yValues = {5.0, 15.0, 25.0};
        TabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);

        CompositeFunction comp = new CompositeFunction(identity, linkedListFunc);
        assertEquals(5.0, comp.apply(0.5), 1e-10);
        assertEquals(15.0, comp.apply(1.5), 1e-10);
        assertEquals(25.0, comp.apply(2.5), 1e-10);
    }

    @Test
    void testCompositeOfTabulatedFunctionsArrayAndArray() {
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {1.0, 4.0, 9.0}; // x²
        double[] xValues2 = {1.0, 4.0, 9.0}; // значения должны совпадать с yValues1
        double[] yValues2 = {2.0, 8.0, 18.0}; // 2x

        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction func2 = new ArrayTabulatedFunction(xValues2, yValues2);

        // f(g(x)) где f и g - табулированные функции
        CompositeFunction comp = new CompositeFunction(func1, func2);

        // func1(1.0) = 1.0, func2(1.0) = 2.0
        assertEquals(2.0, comp.apply(1.0), 1e-10);
        // func1(2.0) = 4.0, func2(4.0) = 8.0
        assertEquals(8.0, comp.apply(2.0), 1e-10);
    }

    @Test
    void testCompositeOfTabulatedFunctionsLinkedListAndLinkedList() {
        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 2.0, 4.0}; // 2x
        double[] xValues2 = {0.0, 2.0, 4.0}; // значения должны совпадать с yValues1
        double[] yValues2 = {0.0, 4.0, 16.0}; // x²

        TabulatedFunction func1 = new LinkedListTabulatedFunction(xValues1, yValues1);
        TabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        CompositeFunction comp = new CompositeFunction(func1, func2);

        // func2(func1(x)) = (2x)² = 4x²
        assertEquals(0.0, comp.apply(0.0), 1e-10);
        assertEquals(4.0, comp.apply(1.0), 1e-10); // (2*1)² = 4
        assertEquals(16.0, comp.apply(2.0), 1e-10); // (2*2)² = 16
    }

    @Test
    void testCompositeOfDifferentTabulatedTypes() {
        double[] xValues1 = {1.0, 2.0, 3.0};
        double[] yValues1 = {2.0, 4.0, 6.0}; // 2x

        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues1, yValues1);

        // Array -> LinkedList: linkedListFunc(arrayFunc(x))
        CompositeFunction comp1 = new CompositeFunction(arrayFunc, linkedListFunc);
        // arrayFunc(2.0) = 4.0, linkedListFunc(4.0) = 8.0
        assertEquals(8.0, comp1.apply(2.0), 1e-10);

        // LinkedList -> Array: arrayFunc(linkedListFunc(x))
        CompositeFunction comp2 = new CompositeFunction(linkedListFunc, arrayFunc);
        // linkedListFunc(2.0) = 4.0, arrayFunc(4.0) = 8.0
        assertEquals(8.0, comp2.apply(2.0), 1e-10);
    }

    @Test
    void testCompositeWithExtrapolation() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0}; // 10x
        TabulatedFunction tabulated = new ArrayTabulatedFunction(xValues, yValues);
        MathFunction sqr = new SqrFunction();

        // sqr(tabulated(x)) с экстраполяцией
        CompositeFunction comp = new CompositeFunction(tabulated, sqr);

        // Экстраполяция слева: tabulated(0.5) = 5.0, sqr(5.0) = 25.0
        assertEquals(25.0, comp.apply(0.5), 1e-10);

        // Экстраполяция справа: tabulated(4.0) = 40.0, sqr(40.0) = 1600.0
        assertEquals(1600.0, comp.apply(4.0), 1e-10);
    }

    @Test
    void testCompositeWithInterpolation() {
        double[] xValues = {1.0, 3.0, 5.0};
        double[] yValues = {2.0, 6.0, 10.0}; // 2x
        TabulatedFunction tabulated = new LinkedListTabulatedFunction(xValues, yValues);
        MathFunction identity = new IdentityFunction();

        // identity(tabulated(x)) с интерполяцией
        CompositeFunction comp = new CompositeFunction(tabulated, identity);

        // Интерполяция: tabulated(2.0) = 4.0, identity(4.0) = 4.0
        assertEquals(4.0, comp.apply(2.0), 1e-10);

        // Интерполяция: tabulated(4.0) = 8.0, identity(8.0) = 8.0
        assertEquals(8.0, comp.apply(4.0), 1e-10);
    }

    @Test
    void testArrayTabulatedFunctionWithCompositeSource() {
        // Создаем сложную функцию: f(x) = (x² + 1)
        MathFunction complexFunc = new MathFunction() {
            @Override
            public double apply(double x) {
                return x * x + 1.0;
            }
        };

        // Табулируем с 3 точками для простоты проверки
        ArrayTabulatedFunction tabulated = new ArrayTabulatedFunction(complexFunc, 0.0, 2.0, 3);

        assertEquals(3, tabulated.getCount());

        // При 3 точках на [0,2]: шаг = (2-0)/(3-1) = 1.0
        // Точки: 0.0, 1.0, 2.0
        assertEquals(0.0, tabulated.getX(0), 1e-10);
        assertEquals(1.0, tabulated.getX(1), 1e-10);
        assertEquals(2.0, tabulated.getX(2), 1e-10);

        // Значения функции: f(x) = x² + 1
        assertEquals(1.0, tabulated.getY(0), 1e-10); // 0² + 1 = 1
        assertEquals(2.0, tabulated.getY(1), 1e-10); // 1² + 1 = 2
        assertEquals(5.0, tabulated.getY(2), 1e-10); // 2² + 1 = 5
    }

    @Test
    void testArrayTabulatedFunctionApplyWithComplexInput() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0}; // x²
        ArrayTabulatedFunction func = new ArrayTabulatedFunction(xValues, yValues);

        // Применяем к результатам другой функции
        MathFunction constant = new ConstantFunction(1.5);
        double result = func.apply(constant.apply(0.0)); // func(1.5)

        // Интерполяция между 1.0 и 2.0: 1.0->1.0, 2.0->4.0, 1.5->2.5
        assertEquals(2.5, result, 1e-10);
    }

    @Test
    void testLinkedListTabulatedFunctionWithCompositeSource() {
        // f(x) = x³
        MathFunction cubeFunc = new MathFunction() {
            @Override
            public double apply(double x) {
                return x * x * x;
            }
        };

        LinkedListTabulatedFunction tabulated = new LinkedListTabulatedFunction(cubeFunc, -1.0, 1.0, 5);

        assertEquals(5, tabulated.getCount());
        assertEquals(-1.0, tabulated.getY(0), 1e-10); // (-1)³ = -1
        assertEquals(0.0, tabulated.getY(2), 1e-10);  // 0³ = 0
        assertEquals(1.0, tabulated.getY(4), 1e-10);  // 1³ = 1
    }

    @Test
    void testLinkedListTabulatedFunctionInCompositeChain() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {1.0, 2.0, 3.0}; // x + 1
        TabulatedFunction tabulated = new LinkedListTabulatedFunction(xValues, yValues);

        // Создаем цепочку: zero -> tabulated -> sqr
        MathFunction zero = new ZeroFunction();
        MathFunction sqr = new SqrFunction();

        CompositeFunction chain = new CompositeFunction(
                zero,
                new CompositeFunction(tabulated, sqr)
        );

        // zero(x) всегда 0, tabulated(0) = 1, sqr(1) = 1
        assertEquals(1.0, chain.apply(100.0), 1e-10);
        assertEquals(1.0, chain.apply(-50.0), 1e-10);
    }

    @Test
    void testConstantFunctionWithTabulated() {
        MathFunction constant = new ConstantFunction(5.0);
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0}; // 10x
        TabulatedFunction tabulated = new ArrayTabulatedFunction(xValues, yValues);

        // constant(tabulated(x)) - всегда 5.0
        CompositeFunction comp1 = new CompositeFunction(tabulated, constant);
        assertEquals(5.0, comp1.apply(1.0), 1e-10);
        assertEquals(5.0, comp1.apply(2.0), 1e-10);
        assertEquals(5.0, comp1.apply(100.0), 1e-10);

        // tabulated(constant(x)) - всегда tabulated(5.0) = экстраполяция
        CompositeFunction comp2 = new CompositeFunction(constant, tabulated);
        // tabulated(5.0) = 50.0 (экстраполяция: 30 + (30-20)/(3-2)*(5-3) = 30 + 10*2 = 50)
        assertEquals(50.0, comp2.apply(999.0), 1e-5);
    }

    @Test
    void testZeroAndUnitFunctionsWithTabulated() {
        MathFunction zero = new ZeroFunction();
        MathFunction unit = new UnitFunction();
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {5.0, 10.0, 15.0}; // 5x + 5
        TabulatedFunction tabulated = new LinkedListTabulatedFunction(xValues, yValues);

        // unit(tabulated(zero(x))) = unit(tabulated(0)) = unit(5) = 1
        CompositeFunction complex = new CompositeFunction(
                zero,
                new CompositeFunction(tabulated, unit)
        );
        assertEquals(1.0, complex.apply(123.0), 1e-10);
    }

    @Test
    void testComplexChainComposition() {
        // Цепочка: zero -> identity -> sqr -> tabulated -> unit
        MathFunction zero = new ZeroFunction();
        MathFunction identity = new IdentityFunction();
        MathFunction sqr = new SqrFunction();

        double[] xValues = {0.0, 1.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0}; // произвольная функция
        TabulatedFunction tabulated = new ArrayTabulatedFunction(xValues, yValues);

        MathFunction unit = new UnitFunction();

        CompositeFunction chain = new CompositeFunction(
                zero,
                new CompositeFunction(
                        identity,
                        new CompositeFunction(
                                sqr,
                                new CompositeFunction(tabulated, unit)
                        )
                )
        );

        // zero(x) = 0, identity(0) = 0, sqr(0) = 0, tabulated(0) = 10, unit(10) = 1
        assertEquals(1.0, chain.apply(999.0), 1e-10);
    }

    @Test
    void testMultipleTabulatedFunctionsInChain() {
        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 2.0, 4.0}; // 2x

        double[] xValues2 = {0.0, 2.0, 4.0}; // значения должны совпадать с yValues1
        double[] yValues2 = {0.0, 4.0, 16.0}; // x²

        double[] xValues3 = {0.0, 4.0, 16.0}; // значения должны совпадать с yValues2
        double[] yValues3 = {1.0, 5.0, 17.0}; // x + 1

        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);
        TabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);
        TabulatedFunction func3 = new ArrayTabulatedFunction(xValues3, yValues3);

        // func3(func2(func1(x))) = ( (2x)² ) + 1 = 4x² + 1
        CompositeFunction chain = new CompositeFunction(
                func1,
                new CompositeFunction(func2, func3)
        );

        assertEquals(1.0, chain.apply(0.0), 1e-10);  // 4*0 + 1 = 1
        assertEquals(5.0, chain.apply(1.0), 1e-10);  // 4*1 + 1 = 5
        assertEquals(17.0, chain.apply(2.0), 1e-10); // 4*4 + 1 = 17
    }

    @Test
    void testEdgeCasesWithTwoPointTabulated() {
        // Табулированная функция с 2 точками (минимальный случай)
        double[] xValues = {1.0, 2.0};
        double[] yValues = {5.0, 5.0}; // константа 5

        TabulatedFunction constantTabulated = new ArrayTabulatedFunction(xValues, yValues);
        MathFunction sqr = new SqrFunction();

        CompositeFunction comp = new CompositeFunction(constantTabulated, sqr);

        // Всегда 25.0, так как constantTabulated(x) всегда 5.0, sqr(5.0) = 25.0
        assertEquals(25.0, comp.apply(0.0), 1e-10);
        assertEquals(25.0, comp.apply(1.0), 1e-10);
        assertEquals(25.0, comp.apply(100.0), 1e-10);
    }
}