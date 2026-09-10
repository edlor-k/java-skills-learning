package ru.korablev.secondmodule.deadlocksimulator;

import ru.korablev.util.Logger;
import ru.korablev.util.ThreadSleepUtil;

public class OrderedLockableResourceProcessor implements LockableResourceProcessor{

    @Override
    public void processResources(LockableResource r1, LockableResource r2) {
        var res1 = r1.getId() < r2.getId() ? r1 : r2;
        var res2 = res1 == r1 ? r2 : r1;
        synchronized (res1.getMonitor()) {
            Logger.log("Взял r%s", r1.getName());
            ThreadSleepUtil.safeSleepWithoutThrow(100);
            synchronized (res2.getMonitor()) {
                Logger.log("Взял r%s", r2.getName());
            }
        }
    }
}
