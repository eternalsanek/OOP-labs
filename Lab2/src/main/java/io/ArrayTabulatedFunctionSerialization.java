package io;

import functions.ArrayTabulatedFunction;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import operations.TabulatedDifferentialOperator;

import java.io.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ArrayTabulatedFunctionSerialization {
    public static void main(String[] args) {
        String filePath = "Lab2/output/serialized array functions.bin";
        log.info("Запуск сериализации ArrayTabulatedFunction в файл: {}", filePath);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath))) {
            log.debug("Файл для записи открыт: {}", filePath);

            double[] xValues = {0.0, 0.5, 1.0, 1.5, 2.0};
            double[] yValues = {0.0, 0.25, 1.0, 2.25, 4.0};
            TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
            log.info("Создана исходная функция: {} точек, диапазон [{}, {}]", function.getCount(), function.leftBound(), function.rightBound());

            TabulatedDifferentialOperator operator =
                    new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

            log.debug("Вычисление первой производной");
            TabulatedFunction firstDerivative = operator.derive(function);
            log.debug("Вычисление второй производной");
            TabulatedFunction secondDerivative = operator.derive(firstDerivative);

            log.info("Сериализация функций в файл");
            FunctionsIO.serialize(bos, function);
            FunctionsIO.serialize(bos, firstDerivative);
            FunctionsIO.serialize(bos, secondDerivative);
            log.info("Сериализация завершена успешно");

        } catch (IOException e) {
            log.error("Ошибка при сериализации в файл {}", filePath, e);
        }

        log.info("Чтение сериализованных функций из файла: {}", filePath);
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath))) {
            log.debug("Файл для чтения открыт: {}", filePath);
            TabulatedFunction f1 = FunctionsIO.deserialize(bis);
            TabulatedFunction f2 = FunctionsIO.deserialize(bis);
            TabulatedFunction f3 = FunctionsIO.deserialize(bis);

            log.info("Десериализованные функции:");
            log.info("Исходная функция: {}", f1);
            log.info("Первая производная: {}", f2);
            log.info("Вторая производная: {}", f3);

        } catch (IOException | ClassNotFoundException e) {
            log.error("Ошибка при десериализации из файла {}", filePath, e);
        }
        log.info("ArrayTabulatedFunctionSerialization завершен");
    }
}