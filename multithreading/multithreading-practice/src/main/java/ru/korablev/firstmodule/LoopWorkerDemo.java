package ru.korablev.firstmodule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.korablev.util.Logger;
import ru.korablev.util.ThreadSleepUtil;

// Задача: воркер с кооперативной остановкой через interrupt().
// В цикле раз в 200-300мс инкремент счётчика и лог состояния.
// При InterruptedException — восстановить флаг прерывания и выйти из цикла (без пустых catch).
public class LoopWorkerDemo {
    static void main() {
        var worker = new LoopWorker("worker-1");
        worker.start();
        ThreadSleepUtil.safeSleepWithoutThrow(2000);
        worker.stopAsync();
    }
}

@Getter
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
class LoopWorker {
    final String name;

    int counter;

    Thread thread;

    public void start() {
        thread = new Thread(() -> {
            while (!thread.isInterrupted()) {
                counter++;
                Logger.log("Incremented. Status: " + thread.getState());
                ThreadSleepUtil.safeSleepRandomMillis(200, 300);
            }
            Logger.log("Interrupted: " + thread.isInterrupted());
        }, name);
        Logger.log("Started. Status: " + thread.getState());
        thread.start();
    }

    public void stopAsync() {
        Logger.log("Stopped. Status: " + thread.getState());
        thread.interrupt();
    }
}
