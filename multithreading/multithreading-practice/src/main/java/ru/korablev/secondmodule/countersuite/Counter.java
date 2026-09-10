package ru.korablev.secondmodule.countersuite;

public interface Counter {
    void increment();
    void incrementBy(long delta);
    long getCount();
}
