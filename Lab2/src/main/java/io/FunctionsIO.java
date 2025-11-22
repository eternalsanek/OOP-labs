package io;

import functions.Point;
import functions.TabulatedFunction;
import functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class FunctionsIO {
    private FunctionsIO() {
        throw new UnsupportedOperationException();
    }

    static void writeTabulatedFunction (BufferedWriter writer, TabulatedFunction function) {
        log.debug("Запись табулированной функции в текстовый формат, количество точек: {}", function.getCount());
        PrintWriter pw = new PrintWriter(writer);

        pw.println(function.getCount());

        for (Point point : function) {
            pw.printf("%f %f%n", point.x, point.y);
        }

        pw.flush();
        log.info("Функция успешно записана в текстовый формат, {} точек", function.getCount());
    }

    static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory)
            throws IOException {
        log.debug("Чтение табулированной функции из текстового формата");

        try {
            int count = Integer.parseInt(reader.readLine());
            log.debug("Чтение функции с {} точками", count);

            double[] xValues = new double[count];
            double[] yValues = new double[count];

            NumberFormat nf = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                if (line == null) {
                    log.error("Неожиданный конец файла при чтении точки {}", i);
                    throw new IOException();
                }

                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    log.error("Неверный формат строки: '{}'", line);
                    throw new IOException();
                }

                xValues[i] = nf.parse(parts[0]).doubleValue();
                yValues[i] = nf.parse(parts[1]).doubleValue();
            }
            log.info("Функция успешно прочитана из текстового формата");
            return factory.create(xValues, yValues);

        } catch (ParseException e) {
            log.error("Ошибка парсинга числа", e);
            throw new IOException("Error parsing number", e);
        }
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function)
            throws IOException {
        log.debug("Запись табулированной функции в бинарный формат, количество точек: {}", function.getCount());
        DataOutputStream dos = new DataOutputStream(outputStream);
        dos.writeInt(function.getCount());
        for (Point point : function) {
            dos.writeDouble(point.x);
            dos.writeDouble(point.y);
        }
        dos.flush();
        log.info("Функция успешно записана в бинарный формат, {} точек", function.getCount());
    }
    public static TabulatedFunction readTabulatedFunction(BufferedInputStream inputStream, TabulatedFunctionFactory factory)
        throws IOException{
        DataInputStream dis = new DataInputStream(inputStream);
        int count = dis.readInt();
        log.debug("Чтение функции с {} точками из бинарного формата", count);
        double[] xValues = new double[count];
        double[] yValues = new double[count];
        for(int i = 0; i < count; i++){
            xValues[i] = dis.readDouble();
            yValues[i] = dis.readDouble();
        }
        log.info("Функция успешно прочитана из бинарного формата");
        return factory.create(xValues, yValues);
    }

    public static void serialize(BufferedOutputStream stream, TabulatedFunction function) throws IOException {
        log.debug("Сериализация функции {} с {} точками", function.getClass().getSimpleName(), function.getCount());
        ObjectOutputStream oos = new ObjectOutputStream(stream);
        oos.writeObject(function);
        oos.flush();
        log.info("Функция {} успешно сериализована, {} точек", function.getClass().getSimpleName(), function.getCount());
    }
    public static TabulatedFunction deserialize(BufferedInputStream stream) throws IOException, ClassNotFoundException {
        log.debug("Десериализация функции");
        ObjectInputStream ois = new ObjectInputStream(stream);
        log.info("Функция успешно десериализована");
        return (TabulatedFunction) ois.readObject();
    }
}