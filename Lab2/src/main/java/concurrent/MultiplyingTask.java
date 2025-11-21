package concurrent;

import functions.TabulatedFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiplyingTask implements Runnable {
    private TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
        log.debug("MultiplyingTask создан для функции с {} точками", function.getCount());
    }

    @Override
    public void run() {
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, 2 * function.getY(i));
            }
        }

        Thread t = Thread.currentThread();
        log.info("Поток {} завершил задание", t.getName());
    }
}