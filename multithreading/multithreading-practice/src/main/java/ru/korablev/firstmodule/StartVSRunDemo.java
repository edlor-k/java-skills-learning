package ru.korablev.firstmodule;

import ru.korablev.util.Logger;
import ru.korablev.util.ThreadJoinUtils;
import ru.korablev.util.ThreadSleepUtil;

import java.util.List;

// Задача: показать разницу run() (выполняется в текущем потоке) и start() (в новом потоке).
// Три задачи: наследник Thread, Runnable, лямбда — каждая со случайной задержкой sleep(10-80мс).
// Логировать getState() потоков до/после старта и во время выполнения, сравнить порядок и имена потоков.
public class StartVSRunDemo {
    static void main() {
        var thread1 = new FirstThread("first-thread");
        var t2 = new SecondThread();
        var thread2 = new Thread(t2, "second-thread");
        var thread3 = new Thread(() -> {
            Logger.log("before sleep, state: " + Thread.currentThread().getState());
            ThreadSleepUtil.safeSleepRandomMillis(10, 80);
            Logger.log("after sleep, state: " + Thread.currentThread().getState());
        }, "third-thread");

        var threadList = List.of(thread1, thread2, thread3);

        Logger.log("main thread: " + Thread.currentThread().getName());
        Logger.log("thread1 before start: " + thread1.getState());
        Logger.log("thread2 before start: " + thread2.getState());
        Logger.log("thread3 before start: " + thread3.getState());

        threadList.forEach(Thread::start);

        Logger.log("thread1 after start: " + thread1.getState());
        Logger.log("thread2 after start: " + thread2.getState());
        Logger.log("thread3 after start: " + thread3.getState());

        ThreadJoinUtils.safeJoin(thread1, thread2, thread3);

        Logger.log("main thread: " + Thread.currentThread().getName());
        Logger.log("thread1 before run: " + thread1.getState());
        Logger.log("thread2 before run: " + thread2.getState());
        Logger.log("thread3 before run: " + thread3.getState());

        threadList.forEach(Thread::run);

        Logger.log("thread1 after run: " + thread1.getState());
        Logger.log("thread2 after run: " + thread2.getState());
        Logger.log("thread3 after run: " + thread3.getState());
    }
}

class FirstThread extends Thread {

    public FirstThread(String name) {
        super(name);
    }

    public void run() {
        Logger.log("before sleep, state: " + Thread.currentThread().getState());
        ThreadSleepUtil.safeSleepRandomMillis(10, 80);
        Logger.log("after sleep, state: " + Thread.currentThread().getState());
    }
}

class SecondThread implements Runnable {

    @Override
    public void run() {
        Logger.log("before sleep, state: " + Thread.currentThread().getState());
        ThreadSleepUtil.safeSleepRandomMillis(10, 80);
        Logger.log("after sleep, state: " + Thread.currentThread().getState());
    }
}

