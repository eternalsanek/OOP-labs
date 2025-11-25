package ru.ssau.tk.NAME.PROJECT.concurrent;

import ru.ssau.tk.NAME.PROJECT.functions.ConstantFunction;
import ru.ssau.tk.NAME.PROJECT.functions.LinkedListTabulatedFunction;
import ru.ssau.tk.NAME.PROJECT.functions.TabulatedFunction;

public class ReadWriteTaskExecutor {
    public static void main(String[] args) {
        ConstantFunction constant = new ConstantFunction(-1.0);
        TabulatedFunction function = new LinkedListTabulatedFunction(constant, 1, 1000, 1000);
        ReadTask taskread = new ReadTask(function);
        WriteTask taskwrite = new WriteTask(function, 0.5);
        Thread threadread = new Thread(taskread);
        Thread threadwrite = new Thread(taskwrite);
        threadread.start();
        threadwrite.start();
        try{
            threadread.join();
            threadwrite.join();
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
