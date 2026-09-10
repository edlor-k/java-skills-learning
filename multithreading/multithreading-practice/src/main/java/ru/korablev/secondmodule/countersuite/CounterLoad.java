package ru.korablev.secondmodule.countersuite;
import ru.korablev.util.Logger;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public final class CounterLoad {
    public static void runLoad(Counter counter, int threadsCount, int
            iterationsPerThread) {
        CyclicBarrier barrier = new CyclicBarrier(threadsCount);
        CountDownLatch latch = new CountDownLatch(threadsCount);
        for (int i = 0; i < threadsCount; i++) {
            new Thread(() -> {
                try {
                    barrier.await();

                    for (int j = 0; j < iterationsPerThread; j++) {
                        counter.increment();
                    }
                } catch (InterruptedException e) {
                    Logger.log("Thread interrupted");
                    Thread.currentThread().interrupt();
                } catch (BrokenBarrierException e) {
                    Logger.log("Barrier was broken");
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        var start = System.nanoTime();
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        var stop = System.nanoTime();
        var timeMs = (stop - start) / 1_000_000;
        var expected = threadsCount * iterationsPerThread;
        Logger.log("<Impl-%s>: expected=<%d>, actual=<%d>, timeMs=<%d>",
                counter.getClass().getSimpleName(), expected, counter.getCount(), timeMs);
    }
}