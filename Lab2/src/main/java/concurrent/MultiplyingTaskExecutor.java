package concurrent;

import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import functions.UnitFunction;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class MultiplyingTaskExecutor {
    public static void main(String[] args) {
        log.info("Запуск MultiplyingTaskExecutor");
        TabulatedFunction function = new LinkedListTabulatedFunction(new UnitFunction(),
                1, 1000, 1000);
        log.debug("Создана табулированная функция: {} точек, диапазон [{}, {}]",
                function.getCount(), function.leftBound(), function.rightBound());

        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(new MultiplyingTask(function), "Multiplier-" + i);
            threads.add(t);
            log.debug("Создан поток: {}", t.getName());
        }
        log.info("Запуск {} потоков для умножения значений", threads.size());

        for (Thread t : threads) {
            t.start();
        }

        try {
            log.debug("Основной поток ожидает завершения рабочих потоков...");
            Thread.sleep(2000);
            log.debug("Ожидание завершено");
        } catch (InterruptedException e) {
            log.error("Основной поток был прерван во время ожидания", e);
            Thread.currentThread().interrupt();
        }

        log.info("MultiplyingTaskExecutor завершен. Финальное состояние функции: {}", function);
    }
}
