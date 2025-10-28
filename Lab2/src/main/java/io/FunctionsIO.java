package io;

import functions.Point;
import functions.TabulatedFunction;

import java.io.*;

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
