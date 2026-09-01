package ru.korablev.firstmodule;

import ru.korablev.util.Logger;
import ru.korablev.util.ThreadJoinUtils;
import ru.korablev.util.ThreadSleepUtil;

// Задача: демон-поток, тикающий в фоне (setDaemon(true) до start()).
// Параллельно запустить 2-3 обычных (user) потока с недолгими вычислениями (1-3с).
// Показать, что JVM завершается, как только закончились user-потоки, не дожидаясь демона.
public class BackgroundTickerDemo {

    static void startDaemon(String name, int periodMillis) {
        var thread = new Thread(() -> {
            while (true) {
                Logger.log("tick");
                ThreadSleepUtil.safeSleepWithoutThrow(periodMillis);
            }
        }, name);
        thread.setDaemon(true);
        thread.start();
    }

    static void main() {
        startDaemon("daemon", 10);
        var t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                if (i % 10 == 0) {
                    Logger.log("Hello from t1");
                    ThreadSleepUtil.safeSleepWithoutThrow(100);
                }
            }
        }, "worker-1");
        var t2 = new Thread(() -> {
            for (int i = 0; i < 500; i+=5) {
                if (i % 15 == 0) {
                    Logger.log("Hello from t2");
                    ThreadSleepUtil.safeSleepWithoutThrow(100);
                }
            }
        }, "worker-2");
        t1.start();
        t2.start();
        ThreadJoinUtils.safeJoin(t1, t2);
    }
}

