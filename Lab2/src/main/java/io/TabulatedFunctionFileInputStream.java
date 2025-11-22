package io;

import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;

import java.io.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TabulatedFunctionFileInputStream {
    public static void main(String[] args){
        String binaryFilePath = "input/binary function.bin";
        log.info("Запуск чтения функций из различных источников");
        log.info("Чтение из бинарного файла: {}", binaryFilePath);
        try(
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(binaryFilePath))
        ){
            log.debug("Бинарный файл открыт для чтения: {}", binaryFilePath);
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(inputStream, new ArrayTabulatedFunctionFactory());
            log.info("Функция прочитана из бинарного файла: {}", arrayFunction);
        }catch(IOException e) {
            log.error("Ошибка при чтении из бинарного файла {}", binaryFilePath, e);
        }
        log.info("Ожидание ввода функции из консоли");
        try{
            System.out.println("Введите размер и значения функции (формат ввода - сначала количество точек, затем пары x y через пробел):");
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            log.debug("Чтение функции из консоли");
            TabulatedFunction listFunction = FunctionsIO.readTabulatedFunction(consoleReader, new LinkedListTabulatedFunctionFactory());
            log.info("Функция прочитана из консоли: {} точек", listFunction.getCount());
            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();
            log.debug("Вычисление производной функции из консоли");
            TabulatedFunction derivative = operator.derive(listFunction);
            log.info("Производная функции вычислена: {}", derivative);
        }catch(IOException e) {
            log.error("Ошибка при чтении функции из консоли", e);
        }
        log.info("TabulatedFunctionFileInputStream завершен");
    }
}
