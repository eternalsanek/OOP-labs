package io;

import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TabulatedFunctionFileReader {
    public static void main(String[] args) {
        try (
                BufferedReader reader1 = new BufferedReader(new FileReader("Lab2/input/function.txt"));
                BufferedReader reader2 = new BufferedReader(new FileReader("Lab2/input/function.txt"))
        ) {
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(reader1, new ArrayTabulatedFunctionFactory());
            TabulatedFunction listFunction = FunctionsIO.readTabulatedFunction(reader2, new LinkedListTabulatedFunctionFactory());

            System.out.println(arrayFunction);
            System.out.println(listFunction);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}