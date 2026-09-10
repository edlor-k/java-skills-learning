package ru.korablev.secondmodule.deadlocksimulator;

import ru.korablev.util.Logger;
import ru.korablev.util.ThreadSleepUtil;

public class WrongLockableResourceProcessor implements LockableResourceProcessor{

    private final long pauseBetweenLocsMs = 100;

    @Override
    public void processResources(LockableResource r1, LockableResource r2) {
        synchronized (r1.getMonitor()) {
            Logger.log("Взял r%s", r1.getName());
            ThreadSleepUtil.safeSleepWithoutThrow(pauseBetweenLocsMs);
            synchronized (r2.getMonitor()) {
                Logger.log("Взял r%s", r2.getName());
            }
        }
    }
}
