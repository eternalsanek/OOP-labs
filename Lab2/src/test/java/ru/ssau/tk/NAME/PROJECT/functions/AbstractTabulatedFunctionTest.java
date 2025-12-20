package ru.ssau.tk.NAME.PROJECT.functions;

import ru.ssau.tk.NAME.PROJECT.exceptions.ArrayIsNotSortedException;
import ru.ssau.tk.NAME.PROJECT.exceptions.DifferentLengthOfArraysException;
import org.junit.jupiter.api.Test;
import ru.ssau.tk.NAME.PROJECT.functions.AbstractTabulatedFunction;

import static org.junit.jupiter.api.Assertions.*;


class AbstractTabulatedFunctionTest {
    @Test
    void checkLengthIsTheSameTest() {
        double[] array1 = {1, 89, 100, 152};
        double[] array2 = {45, 78, 56};
        assertThrows(DifferentLengthOfArraysException.class,
                () -> AbstractTabulatedFunction.checkLengthIsTheSame(array1, array2));

        double[] array3 = {1, 89, 100, 152};
        double[] array4 = {45, 78, 123, 487};
        assertDoesNotThrow(() -> AbstractTabulatedFunction.checkLengthIsTheSame(array3, array4));
    }

    @Test
    void checkSortedTest() {
        double[] array1 = {1, 89, 100, 152};
        assertDoesNotThrow(() -> AbstractTabulatedFunction.checkSorted(array1));

        double[] array2 = {45, 78, 56};
        assertThrows(ArrayIsNotSortedException.class, () -> AbstractTabulatedFunction.checkSorted(array2));

        double[] array3 = {1, 1, 2};
        assertThrows(ArrayIsNotSortedException.class, () -> AbstractTabulatedFunction.checkSorted(array3));
    }
}