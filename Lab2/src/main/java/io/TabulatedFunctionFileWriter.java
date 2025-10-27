package io;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TabulatedFunctionFileWriter {
    public static void main(String[] args) {
        try (
                BufferedWriter arrayWriter = new BufferedWriter(
                        new FileWriter("Lab2/output/array function.txt"));
                BufferedWriter listWriter = new BufferedWriter(
                        new FileWriter("Lab2/output/linked list function.txt"))
        ) {
            // Создаём две функции
            double[] xValues = {0.0, 0.5, 1.0};
            double[] yValues = {0.0, 0.25, 1.0};

            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
            TabulatedFunction listFunc = new LinkedListTabulatedFunction(xValues, yValues);

            // Записываем в файлы
            FunctionsIO.writeTabulatedFunction(arrayWriter, arrayFunc);
            FunctionsIO.writeTabulatedFunction(listWriter, listFunc);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
