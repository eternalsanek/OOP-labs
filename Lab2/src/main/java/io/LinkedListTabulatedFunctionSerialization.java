package io;

import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;

import java.io.*;

public class LinkedListTabulatedFunctionSerialization {
    public static void main(String[] args){
        try(
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream("output/serialized linked list functions.bin"))
        ){
            TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
            /*
            double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
            double[] yValues = {2.0, 4.0, 6.0, 8.0, 10.0};
            */
            double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
            double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};
            LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);
            TabulatedFunction derivative1 = operator.derive(function);
            TabulatedFunction derivative2 = operator.derive(derivative1);
            FunctionsIO.serialize(outputStream, function);
            FunctionsIO.serialize(outputStream, derivative1);
            FunctionsIO.serialize(outputStream, derivative2);
        }catch (IOException e){
            e.printStackTrace();
        }
        try(
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("output/serialized linked list functions.bin"))
        ){
            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(inputStream);
            TabulatedFunction deserializedDerivative1 = FunctionsIO.deserialize(inputStream);
            TabulatedFunction deserializedDerivative2 = FunctionsIO.deserialize(inputStream);
            System.out.println("\nИсходная функция:");
            System.out.println(deserializedFunction.toString());
            System.out.println("\n1-я производная:");
            System.out.println(deserializedDerivative1.toString());
            System.out.println("\n2-я производная:");
            System.out.println(deserializedDerivative2.toString());
        }catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
    }
}
