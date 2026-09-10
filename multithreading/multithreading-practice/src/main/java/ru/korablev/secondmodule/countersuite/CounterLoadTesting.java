package ru.korablev.secondmodule.countersuite;

// Задача: сравнить 5 потокобезопасных реализаций Counter (Unsafe/Synchronized/Atomic/ReentrantLock/LongAdder)
// по корректности и скорости под нагрузкой через CounterLoad.runLoad(counter, threads, itersPerThread).
// Шаг 1: threads=8, itersPerThread=250_000 — прогнать все пять; ожидание: у Unsafe actual < expected, у остальных actual == expected.
// Шаг 2: threads=32, itersPerThread=1_000_000 — прогнать только Atomic и LongAdder, сравнить timeMs (LongAdder не должен уступать Atomic).
public class CounterLoadTesting {

    private final static int THREADS_COUNT = 8;
    private final static int ITERATIONS_PER_THREAD = 250_000;

    static void main() {
        var unsafeCounter = new UnsafeCounter();
        var synchronizedCounter = new SynchronizedCounter();
        var atomicCounter = new AtomicCounter();
        var reentrantLockCounter = new ReentrantLockCounter();
        var longAdderCounter = new LongAdderCounter();
        runLoadWithConstants(unsafeCounter);
        runLoadWithConstants(synchronizedCounter);
        runLoadWithConstants(atomicCounter);
        runLoadWithConstants(reentrantLockCounter);
        runLoadWithConstants(longAdderCounter);
    }

    static void runLoadWithConstants(Counter cn) {
        CounterLoad.runLoad(cn, THREADS_COUNT, ITERATIONS_PER_THREAD);
    }
}
