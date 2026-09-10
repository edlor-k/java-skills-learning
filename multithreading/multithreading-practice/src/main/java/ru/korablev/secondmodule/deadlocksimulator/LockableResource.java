package ru.korablev.secondmodule.deadlocksimulator;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
@Data
public final class LockableResource {
    private final String name;
    private final int id;
    private final ReentrantLock lock = new ReentrantLock();
    private final Object monitor = new Object();
}
