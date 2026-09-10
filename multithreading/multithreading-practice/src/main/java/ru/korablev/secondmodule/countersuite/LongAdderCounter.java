package ru.korablev.secondmodule.countersuite;

import java.util.concurrent.atomic.LongAdder;

public class LongAdderCounter implements Counter{

    private final LongAdder count = new LongAdder();

    @Override
    public void increment() {
        incrementBy(1L);
    }

    @Override
    public void incrementBy(long delta) {
        count.add(delta);
    }

    @Override
    public long getCount() {
        return count.longValue();
    }
}
