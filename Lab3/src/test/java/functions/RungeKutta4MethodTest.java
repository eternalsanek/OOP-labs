package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RungeKutta4MethodTest {

    @Test
    void testApplyExactInitialPoint() {
        OrdinaryDifferentialEquation ode = (x, y) -> 1.0;
        RungeKutta4Method solver = new RungeKutta4Method(ode, 0.0, 1.0, 0.1);
        assertEquals(1.0, solver.apply(0.0), 1e-10);
    }

    @Test
    void testApplyLinearEquation() {
        OrdinaryDifferentialEquation ode = (x, y) -> 1.0;
        RungeKutta4Method solver = new RungeKutta4Method(ode, 0.0, 0.0, 0.01);
        assertEquals(0.0, solver.apply(0.0), 1e-10);
        assertEquals(1.0, solver.apply(1.0), 1e-8);
        assertEquals(2.5, solver.apply(2.5), 1e-8);
        assertEquals(-1.0, solver.apply(-1.0), 1e-8);
    }

    @Test
    void testApplyExponentialEquation() {
        OrdinaryDifferentialEquation ode = (x, y) -> y;
        RungeKutta4Method solver = new RungeKutta4Method(ode, 0.0, 1.0, 0.001);
        assertEquals(1.0, solver.apply(0.0), 1e-10);
        assertEquals(Math.exp(1.0), solver.apply(1.0), 1e-6);
        assertEquals(Math.exp(2.0), solver.apply(2.0), 1e-5);
        assertEquals(Math.exp(-1.0), solver.apply(-1.0), 1e-6);
    }

    @Test
    void testApplyQuadraticEquation() {
        OrdinaryDifferentialEquation ode = (x, y) -> 2 * x;
        RungeKutta4Method solver = new RungeKutta4Method(ode, 0.0, 0.0, 0.01);
        assertEquals(0.0, solver.apply(0.0), 1e-10);
        assertEquals(1.0, solver.apply(1.0), 1e-6);
        assertEquals(4.0, solver.apply(2.0), 1e-6);
        assertEquals(9.0, solver.apply(3.0), 1e-5);
    }

    @Test
    void testApplyBackwardIntegration() {
        OrdinaryDifferentialEquation ode = (x, y) -> 1.0;
        RungeKutta4Method solver = new RungeKutta4Method(ode, 1.0, 2.0, 0.1);
        assertEquals(1.0, solver.apply(0.0), 1e-8);
        assertEquals(2.0, solver.apply(1.0), 1e-10);
        assertEquals(2.5, solver.apply(1.5), 1e-8);
    }

    @Test
    void testApplyWithDifferentStepSizes() {
        OrdinaryDifferentialEquation ode = (x, y) -> 2 * x;
        RungeKutta4Method solverLargeStep = new RungeKutta4Method(ode, 0.0, 0.0, 0.5);
        RungeKutta4Method solverSmallStep = new RungeKutta4Method(ode, 0.0, 0.0, 0.01);
        double exact = 4.0;
        double resultLarge = solverLargeStep.apply(2.0);
        double resultSmall = solverSmallStep.apply(2.0);
        assertEquals(exact, resultLarge, 0.1);
        assertEquals(exact, resultSmall, 1e-4);
        double errorLarge = Math.abs(resultLarge - exact);
        double errorSmall = Math.abs(resultSmall - exact);
        System.out.println("Error with large step: " + errorLarge);
        System.out.println("Error with small step: " + errorSmall);
    }

    @Test
    void testApplyConstantFunction() {
        OrdinaryDifferentialEquation ode = (x, y) -> 0.0;
        RungeKutta4Method solver = new RungeKutta4Method(ode, 0.0, 5.0, 0.1);
        assertEquals(5.0, solver.apply(0.0), 1e-10);
        assertEquals(5.0, solver.apply(10.0), 1e-10);
        assertEquals(5.0, solver.apply(-5.0), 1e-10);
        assertEquals(5.0, solver.apply(100.0), 1e-10);
    }

    @Test
    void testApplyTrigonometricEquation() {
        OrdinaryDifferentialEquation ode = (x, y) -> Math.cos(x);
        RungeKutta4Method solver = new RungeKutta4Method(ode, 0.0, 0.0, 0.001);
        assertEquals(0.0, solver.apply(0.0), 1e-10);
        assertEquals(Math.sin(1.0), solver.apply(1.0), 1e-5);
        assertEquals(Math.sin(Math.PI/2), solver.apply(Math.PI/2), 1e-5);
        assertEquals(Math.sin(-1.0), solver.apply(-1.0), 1e-5);
    }

    @Test
    void testApplyWithVerySmallStep() {
        OrdinaryDifferentialEquation ode = (x, y) -> 2 * x;
        RungeKutta4Method solver = new RungeKutta4Method(ode, 0.0, 0.0, 1e-6);
        assertEquals(1.0, solver.apply(1.0), 1e-10);
        assertEquals(4.0, solver.apply(2.0), 1e-9);
    }

    @Test
    void testApplyComplexEquation() {
        OrdinaryDifferentialEquation ode = (x, y) -> x + y;
        RungeKutta4Method solver = new RungeKutta4Method(ode, 0.0, 1.0, 0.001);
        double exact1 = 2*Math.exp(1.0) - 1.0 - 1;
        double exact2 = 2*Math.exp(2.0) - 2.0 - 1;
        assertEquals(exact1, solver.apply(1.0), 1e-4);
        assertEquals(exact2, solver.apply(2.0), 1e-3);
    }

    @Test
    void testApplyCloseToInitialPoint() {
        OrdinaryDifferentialEquation ode = (x, y) -> 1.0;
        RungeKutta4Method solver = new RungeKutta4Method(ode, 0.0, 0.0, 0.1);
        assertEquals(0.0, solver.apply(1e-11), 1e-10);
        assertEquals(0.0, solver.apply(-1e-11), 1e-10);
        assertTrue(solver.apply(1e-9) > 0);
    }
}