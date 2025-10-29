package io;

import functions.ArrayTabulatedFunction;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;

import java.io.*;

public class ArrayTabulatedFunctionSerialization {
    public static void main(String[] args) {
        String filePath = "Lab2/output/serialized array functions.bin";
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {

            double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
            double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};
            TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

            TabulatedDifferentialOperator operator =
                    new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

            TabulatedFunction firstDerivative = operator.derive(function);
            TabulatedFunction secondDerivative = operator.derive(firstDerivative);

            FunctionsIO.serialize(bos, function);
            FunctionsIO.serialize(bos, firstDerivative);
            FunctionsIO.serialize(bos, secondDerivative);

        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
            TabulatedFunction f1 = FunctionsIO.deserialize(bis);
            TabulatedFunction f2 = FunctionsIO.deserialize(bis);
            TabulatedFunction f3 = FunctionsIO.deserialize(bis);

            System.out.println(f1);
            System.out.println(f2);
            System.out.println(f3);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}