package io;

import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;

import java.io.*;

public class TabulatedFunctionFileInputStream {
    public static void main(String[] args){
        try(
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream("input/binary function.bin"))
        ){
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(inputStream, new ArrayTabulatedFunctionFactory());
            System.out.println("Функция из бинарного файла:");
            System.out.println(arrayFunction.toString());
        }catch(IOException e) {
            e.printStackTrace();
        }
        try{
            System.out.println("Введите размер и значения функции (формат ввода - сначала количество точек, затем пары x y через пробел):");
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            TabulatedFunction listFunction = FunctionsIO.readTabulatedFunction(consoleReader, new LinkedListTabulatedFunctionFactory());
            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
            System.out.println("Производная функции:");
            System.out.println(operator.derive(listFunction).toString());
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
}
