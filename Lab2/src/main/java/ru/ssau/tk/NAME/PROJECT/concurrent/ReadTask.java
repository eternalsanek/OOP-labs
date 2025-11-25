package ru.ssau.tk.NAME.PROJECT.concurrent;

import ru.ssau.tk.NAME.PROJECT.functions.TabulatedFunction;

public class ReadTask implements Runnable {
    private TabulatedFunction function;
    public ReadTask(TabulatedFunction function){
        this.function = function;
    }
    @Override
    public void run(){
        for(int i = 0; i < function.getCount(); i++){
            synchronized (function){
                System.out.printf("After read: i = %d, x = %f, y = %f%n", i, function.getX(i), function.getY(i));
            }
            try{
                Thread.sleep(1);
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
