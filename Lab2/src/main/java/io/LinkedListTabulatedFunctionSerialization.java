package io;

import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;

import java.io.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LinkedListTabulatedFunctionSerialization {
    public static void main(String[] args){
        String filePath = "output/serialized linked list functions.bin";
        log.info("Запуск сериализации LinkedListTabulatedFunction в файл: {}", filePath);
        try(
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(filePath))
        ){
            log.debug("Файл для записи открыт: {}", filePath);
            TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
            double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
            double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};
            LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
            log.info("Создана исходная функция: {} точек, диапазон [{}, {}]", function.getCount(), function.leftBound(), function.rightBound());
            TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);
            log.debug("Вычисление производных");
            TabulatedFunction derivative1 = operator.derive(function);
            TabulatedFunction derivative2 = operator.derive(derivative1);
            log.info("Сериализация функций в файл");
            FunctionsIO.serialize(outputStream, function);
            FunctionsIO.serialize(outputStream, derivative1);
            FunctionsIO.serialize(outputStream, derivative2);
            log.info("Сериализация завершена успешно");
        }catch (IOException e){
            log.error("Ошибка при сериализации в файл {}", filePath, e);
        }
        log.info("Чтение сериализованных функций из файла: {}", filePath);
        try(
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(filePath))
        ){
            log.debug("Файл для чтения открыт: {}", filePath);
            TabulatedFunction deserializedFunction = FunctionsIO.deserialize(inputStream);
            TabulatedFunction deserializedDerivative1 = FunctionsIO.deserialize(inputStream);
            TabulatedFunction deserializedDerivative2 = FunctionsIO.deserialize(inputStream);
            log.info("Десериализованные функции:");
            log.info("Исходная функция: {}", deserializedFunction);
            log.info("Первая производная: {}", deserializedDerivative1);
            log.info("Вторая производная: {}", deserializedDerivative2);
        }catch (IOException | ClassNotFoundException e){
            log.error("Ошибка при десериализации из файла {}", filePath, e);
        }
        log.info("LinkedListTabulatedFunctionSerialization завершен");
    }
}
