package io;

import functions.Point;
import functions.TabulatedFunction;
import java.io.BufferedWriter;
import java.io.PrintWriter;

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
}
