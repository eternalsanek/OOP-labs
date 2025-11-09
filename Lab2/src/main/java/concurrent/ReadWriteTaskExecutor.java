package concurrent;

import functions.ConstantFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;

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
