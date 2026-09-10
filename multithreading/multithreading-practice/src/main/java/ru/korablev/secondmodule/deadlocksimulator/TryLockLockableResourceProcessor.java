package ru.korablev.secondmodule.deadlocksimulator;

import ru.korablev.util.Logger;
import ru.korablev.util.ThreadSleepUtil;

import java.util.concurrent.TimeUnit;

public class TryLockLockableResourceProcessor implements LockableResourceProcessor{

    private final long tryLockTimeoutMs = 200;
    private final long backOffMs = 200;

    @Override
    public void processResources(LockableResource r1, LockableResource r2) throws InterruptedException {
        while(true) {
            if (!r1.getLock().tryLock(tryLockTimeoutMs, TimeUnit.MILLISECONDS)) {
                Logger.log("Не взял r%s, откатываюсь", r1.getName());
                ThreadSleepUtil.safeSleepWithoutThrow(backOffMs);
                continue;
            }
            try {
                Logger.log("Взял r%s", r1.getName());
                if (!r2.getLock().tryLock(tryLockTimeoutMs, TimeUnit.MILLISECONDS)) {
                    Logger.log("Не взял r%s, откатываюсь", r2.getName());
                    ThreadSleepUtil.safeSleepWithoutThrow(backOffMs);
                    continue;
                }
                try {
                    Logger.log("Взял r%s", r2.getName());
                    return;
                } finally {
                    r2.getLock().unlock();
                }
            } finally {
                r1.getLock().unlock();
            }
        }
    }
}
