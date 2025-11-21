package concurrent;

import functions.TabulatedFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WriteTask implements Runnable {
    private TabulatedFunction function;
    private double value;
    public WriteTask(TabulatedFunction function, double value){
        this.function = function;
        this.value = value;
        log.debug("WriteTask создан для функции с {} точками, значение для записи: {}", function.getCount(), value);
    }
    @Override
    public void run() {
        Thread currentThread = Thread.currentThread();
        log.info("Поток {} начал запись значения {} в функцию", currentThread.getName(), value);
        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                function.setY(i, value);
                log.debug("Запись для индекса {} завершена", i);
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                log.warn("Поток {} был прерван во время записи", currentThread.getName());
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.info("Поток {} завершил запись", currentThread.getName());
    }
}
