package concurrent;

import functions.TabulatedFunction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadTask implements Runnable {
    private TabulatedFunction function;
    public ReadTask(TabulatedFunction function){
        this.function = function;
        log.debug("ReadTask создан для функции с {} точками", function.getCount());
    }
    @Override
    public void run(){
        Thread currentThread = Thread.currentThread();
        log.info("Поток {} начал чтение значений функции", currentThread.getName());
        for(int i = 0; i < function.getCount(); i++){
            synchronized (function){
                log.debug("Прочитано: i = {}, x = {}, y = {}", i, function.getX(i), function.getY(i));
            }
            try{
                Thread.sleep(1);
            }catch (InterruptedException e){
                log.warn("Поток {} был прерван во время чтения", currentThread.getName());
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.info("Поток {} завершил чтение", currentThread.getName());
    }
}
