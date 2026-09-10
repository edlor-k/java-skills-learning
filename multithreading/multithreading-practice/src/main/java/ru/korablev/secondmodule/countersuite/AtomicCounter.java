package ru.korablev.secondmodule.countersuite;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicCounter implements Counter{

    private final AtomicLong count = new AtomicLong(0L);

    @Override
    public void increment() {
        incrementBy(1L);
    }

    @Override
    public void incrementBy(long delta) {
        count.addAndGet(delta);
    }

    @Override
    public long getCount() {
        return count.get();
    }
}
