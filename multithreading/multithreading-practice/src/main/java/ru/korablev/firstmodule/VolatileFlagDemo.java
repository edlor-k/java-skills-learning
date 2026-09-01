package ru.korablev.firstmodule;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.korablev.util.Logger;
import ru.korablev.util.ThreadSleepUtil;

// Задача: показать, что volatile даёт видимость изменений между потоками, но не атомарность.
// Поток крутится в while(running) без синхронизации; running помечен volatile.
// stop() выставляет running=false — поток должен корректно завершиться.
public class VolatileFlagDemo {
    static void main() {
        var flagDemo = new VolatileFlag();
        flagDemo.start();
        ThreadSleepUtil.safeSleepWithoutThrow(500);
        flagDemo.stop();
    }
}

@FieldDefaults(level = AccessLevel.PRIVATE)
class VolatileFlag {
    volatile boolean running;
    Thread thread = new Thread(() -> {
        while(running) {
        }
        Logger.log("Thread stopped");
    });
    void start() {
        running = true;
        Logger.log("Thread started");
        thread.start();
    }

    void stop() {
        running = false;
    }
}
