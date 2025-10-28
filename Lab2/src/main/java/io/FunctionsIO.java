package io;

import functions.Point;
import functions.TabulatedFunction;
import functions.factory.TabulatedFunctionFactory;

import java.io.*;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public final class FunctionsIO {
    private FunctionsIO() {
        throw new UnsupportedOperationException();
    }

    static void writeTabulatedFunction (BufferedWriter writer, TabulatedFunction function) {
        PrintWriter pw = new PrintWriter(writer);

        pw.println(function.getCount());

        for (Point point : function) {
            pw.printf("%f %f%n", point.x, point.y);
        }

        pw.flush();
    }

    static TabulatedFunction readTabulatedFunction(BufferedReader reader, TabulatedFunctionFactory factory)
            throws IOException {

        try {
            int count = Integer.parseInt(reader.readLine());

            double[] xValues = new double[count];
            double[] yValues = new double[count];

            NumberFormat nf = NumberFormat.getInstance(Locale.forLanguageTag("ru"));

            for (int i = 0; i < count; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("Unexpected end of file");
                }

                String[] parts = line.split(" ");
                if (parts.length != 2) {
                    throw new IOException("Invalid line format: " + line);
                }

                xValues[i] = nf.parse(parts[0]).doubleValue();
                yValues[i] = nf.parse(parts[1]).doubleValue();
            }

            return factory.create(xValues, yValues);

        } catch (ParseException e) {
            throw new IOException("Error parsing number", e);
        }
    }

    public static void writeTabulatedFunction(BufferedOutputStream outputStream, TabulatedFunction function)
            throws IOException {
        DataOutputStream dos = new DataOutputStream(outputStream);
        dos.writeInt(function.getCount());
        for (Point point : function) {
            dos.writeDouble(point.x);
            dos.writeDouble(point.y);
        }
        dos.flush();
    }
}