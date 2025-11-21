package io;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TabulatedFunctionFileOutputStream {
    public static void main(String[] args) {
        log.info("Запуск записи функций в бинарные файлы");
        try(
                BufferedOutputStream arrayStream = new BufferedOutputStream(new FileOutputStream("output/array function.bin"));
                BufferedOutputStream listStream = new BufferedOutputStream(new FileOutputStream("output/linked list function.bin"))
        ){
            log.debug("Бинарные файлы для записи открыты");
            double[] xValues = {0.0, 0.5, 1.0};
            double[] yValues = {0.0, 0.25, 1.0};
            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);
            TabulatedFunction listFunc = new LinkedListTabulatedFunction(xValues, yValues);
            log.info("Созданы функции: ArrayTabulatedFunction ({} точек) и LinkedListTabulatedFunction ({} точек)", arrayFunc.getCount(), listFunc.getCount());
            log.debug("Запись ArrayTabulatedFunction в бинарный файл");
            FunctionsIO.writeTabulatedFunction(arrayStream, arrayFunc);
            log.debug("Запись LinkedListTabulatedFunction в бинарный файл");
            FunctionsIO.writeTabulatedFunction(listStream, listFunc);
            log.info("Функции успешно записаны в бинарные файлы");
        } catch(IOException e) {
            log.error("Ошибка при записи функций в бинарные файлы", e);
        }
        log.info("TabulatedFunctionFileOutputStream завершен");
    }
}
