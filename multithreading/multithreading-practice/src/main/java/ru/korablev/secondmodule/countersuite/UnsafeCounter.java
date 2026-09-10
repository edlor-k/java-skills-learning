package ru.korablev.secondmodule.countersuite;

public class UnsafeCounter implements Counter{

    private long count = 0;

    @Override
    public void increment() {
        incrementBy(1L);
    }

    @Override
    public void incrementBy(long delta) {
        count += delta;
    }

    @Override
    public long getCount() {
        return count;
    }
}
