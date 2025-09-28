package functions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentityFunctionTest {
    @Test
    public void testApply() {
        IdentityFunction function = new IdentityFunction();
        assertEquals(0.0, function.apply(0.0), 1e-10);
        assertEquals(5.0, function.apply(5.0), 1e-10);
        assertEquals(0.5, function.apply(0.5), 1e-10);
        assertEquals(-5.0, function.apply(-5.0), 1e-10);
        assertEquals(-0.5, function.apply(-0.5), 1e-10);
        assertEquals(5.5, function.apply(5.5), 1e-10);
        assertEquals(-5.5, function.apply(-5.5), 1e-10);
        assertEquals(100.5, function.apply(100.5), 1e-10);
    }
}