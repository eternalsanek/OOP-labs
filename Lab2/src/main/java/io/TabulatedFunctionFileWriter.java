package io;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TabulatedFunctionFileWriter {
    public static void main(String[] args) {
        log.info("Запуск записи функций в текстовые файлы");
        try (
                BufferedWriter arrayWriter = new BufferedWriter(
                        new FileWriter("Lab2/output/array function.txt"));
                BufferedWriter listWriter = new BufferedWriter(
                        new FileWriter("Lab2/output/linked list function.txt"))
        ) {
            log.debug("Файлы для записи открыты");
            // Создаём две функции
            double[] xValues = {0.0, 0.5, 1.0};
            double[] yValues = {0.0, 0.25, 1.0};

            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
            TabulatedFunction listFunc = new LinkedListTabulatedFunction(xValues, yValues);
            log.info("Созданы функции: ArrayTabulatedFunction ({} точек) и LinkedListTabulatedFunction ({} точек)", arrayFunc.getCount(), listFunc.getCount());

            // Записываем в файлы
            log.debug("Запись ArrayTabulatedFunction в файл");
            FunctionsIO.writeTabulatedFunction(arrayWriter, arrayFunc);
            log.debug("Запись LinkedListTabulatedFunction в файл");
            FunctionsIO.writeTabulatedFunction(listWriter, listFunc);
            log.info("Функции успешно записаны в текстовые файлы");

        } catch (IOException e) {
            log.error("Ошибка при записи функций в файлы", e);
        }
        log.info("TabulatedFunctionFileWriter завершен");
    }
}
