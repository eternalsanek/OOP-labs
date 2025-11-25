package ru.ssau.tk.NAME.PROJECT.io;

import ru.ssau.tk.NAME.PROJECT.functions.ArrayTabulatedFunction;
import ru.ssau.tk.NAME.PROJECT.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.NAME.PROJECT.functions.TabulatedFunction;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class TabulatedFunctionFileOutputStream {
    public static void main(String[] args) {
        try(
                BufferedOutputStream arrayStream = new BufferedOutputStream(new FileOutputStream("output/array function.bin"));
                BufferedOutputStream listStream = new BufferedOutputStream(new FileOutputStream("output/linked list function.bin"))
        ){
            double[] xValues = {0.0, 0.5, 1.0};
            double[] yValues = {0.0, 0.25, 1.0};
            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
            TabulatedFunction listFunc = new LinkedListTabulatedFunction(xValues, yValues);
            FunctionsIO.writeTabulatedFunction(arrayStream, arrayFunc);
            FunctionsIO.writeTabulatedFunction(listStream, listFunc);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
