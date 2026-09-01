package ru.korablev.firstmodule;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ru.korablev.util.Logger;
import ru.korablev.util.ThreadJoinUtils;

import java.util.ArrayList;

// Задача: сравнить гонку без синхронизации и с ней.
// UnsafeCounter — инкремент value++ без защиты; SynchronizedCounter — инкремент в synchronized.
// runRace(N, M) запускает N потоков по M инкрементов и сравнивает expected с actual.
public class MutexCounterDemo {

    static Counter cnt;

    static void main() {
        cnt = new UnsafeCounter();
        runRace(8, 100_000);
        cnt = new SynchronizedCounter();
        runRace(8, 100_000);
    }

    static void runRace(int threads, int itersPerThread) {
        var list = new ArrayList<Thread>();
        for (int i = 0; i < threads; i ++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < itersPerThread; j++) {
                    cnt.inc();
                }
            });
            list.add(thread);
        }
        list.forEach(Thread::start);
        list.forEach(ThreadJoinUtils::safeJoin);
        long expected = (long) threads * itersPerThread;
        Logger.log("Expected: " + expected + ", Actual: " + cnt.value());
    }
}

interface Counter {
    void inc();
    long value();
}

@FieldDefaults(level = AccessLevel.PRIVATE)
class UnsafeCounter implements Counter {

    long value = 0;

    @Override
    public void inc() {
        value++;
    }

    @Override
    public long value() {
        return value;
    }
}

@FieldDefaults(level = AccessLevel.PRIVATE)
class SynchronizedCounter implements Counter {

    long value = 0;

    @Override
    public synchronized void inc() {
        value++;
    }

    @Override
    public long value() {
        return value;
    }
}