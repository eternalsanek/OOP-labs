package io;

import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TabulatedFunctionFileReader {
    public static void main(String[] args) {
        String filePath = "Lab2/input/function.txt";
        log.info("Запуск чтения функций из текстового файла: {}", filePath);
        try (
                BufferedReader reader1 = new BufferedReader(new FileReader(filePath));
                BufferedReader reader2 = new BufferedReader(new FileReader(filePath))
        ) {
            log.debug("Файл открыт для чтения: {}", filePath);
            log.debug("Чтение функции как ArrayTabulatedFunction");
            TabulatedFunction arrayFunction = FunctionsIO.readTabulatedFunction(reader1, new ArrayTabulatedFunctionFactory());
            log.debug("Чтение функции как LinkedListTabulatedFunction");
            TabulatedFunction listFunction = FunctionsIO.readTabulatedFunction(reader2, new LinkedListTabulatedFunctionFactory());

            log.info("Прочитанные функции:");
            log.info("ArrayTabulatedFunction: {}", arrayFunction);
            log.info("LinkedListTabulatedFunction: {}", listFunction);

        } catch (IOException e) {
            log.error("Ошибка при чтении функций из файла {}", filePath, e);
        }
        log.info("TabulatedFunctionFileReader завершен");
    }
}