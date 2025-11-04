package concurrent;

import functions.TabulatedFunction;

public class MultiplyingTask implements Runnable {
    private TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, 2 * function.getY(i));
            }
        }

        Thread t = Thread.currentThread();
        System.out.println("Flow " + t.getName() + " completed the task");
    }
}