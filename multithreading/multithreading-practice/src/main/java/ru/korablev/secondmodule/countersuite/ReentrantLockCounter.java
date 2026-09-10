package ru.korablev.secondmodule.countersuite;

import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockCounter implements Counter {

    private long count = 0;

    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public void increment() {
        incrementBy(1L);
    }

    @Override
    public void incrementBy(long delta) {
        lock.lock();
        try {
            count += delta;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getCount() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }
}
