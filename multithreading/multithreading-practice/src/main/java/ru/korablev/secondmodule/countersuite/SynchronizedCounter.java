package ru.korablev.secondmodule.countersuite;

public class SynchronizedCounter implements Counter{

    private long count = 0;

    @Override
    public void increment() {
        incrementBy(1L);
    }

    @Override
    public synchronized void incrementBy(long delta) {
        count += delta;
    }

    @Override
    public synchronized long getCount() {
        return count;
    }
}
