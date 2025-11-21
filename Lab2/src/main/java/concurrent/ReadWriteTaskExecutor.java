package concurrent;

import functions.ConstantFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadWriteTaskExecutor {
    public static void main(String[] args) {
        log.info("Запуск ReadWriteTaskExecutor - демонстрация чтения/записи");
        ConstantFunction constant = new ConstantFunction(-1.0);
        TabulatedFunction function = new LinkedListTabulatedFunction(constant, 1, 1000, 1000);
        log.debug("Инициализирована функция константой {}, {} точек", -1.0, function.getCount());
        ReadTask taskread = new ReadTask(function);
        WriteTask taskwrite = new WriteTask(function, 0.5);
        Thread threadread = new Thread(taskread);
        Thread threadwrite = new Thread(taskwrite);
        log.info("Запуск потоков чтения и записи");
        threadread.start();
        threadwrite.start();
        try{
            log.debug("Основной поток ожидает завершения потоков чтения/записи");
            threadread.join();
            threadwrite.join();
            log.info("Все потоки успешно завершили работу");
        }catch (InterruptedException e){
            log.error("Основной поток был прерван во время ожидания", e);
            Thread.currentThread().interrupt();
        }
        log.info("ReadWriteTaskExecutor завершен");
    }
}
