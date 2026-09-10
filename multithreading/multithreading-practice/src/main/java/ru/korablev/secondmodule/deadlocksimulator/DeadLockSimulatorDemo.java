package ru.korablev.secondmodule.deadlocksimulator;

import ru.korablev.util.ThreadJoinUtils;

// Задача: намеренно воспроизвести дедлок между двумя потоками на двух LockableResource и показать два фикса.
// A) WrongLockableResourceProcessor — T1 берёт r1→r2, T2 берёт r2→r1, между захватами пауза — должен воспроизводиться дедлок (join() виснет).
// B) OrderedLockableResourceProcessor — тот же synchronized, но захват всегда по возрастанию id — дедлок исключён.
// C) TryLockLockableResourceProcessor — ReentrantLock.tryLock(timeout) + backoff вместо блокирующего захвата — дедлок исключён, в логах видны ретраи.
// Переключатель SCENARIO выбирает, какую стратегию запустить: сценарии нельзя гонять в одном прогоне —
// Wrong вешает join(), поэтому меняйте константу и перезапускайте main по очереди.
public class DeadLockSimulatorDemo {

    private enum Scenario {WRONG, ORDERED, TRY_LOCK}

    private static final Scenario SCENARIO = Scenario.TRY_LOCK;
    private static final int ITERATIONS = 1000;

    static void main() {
        LockableResource rA = new LockableResource("A", 1);
        LockableResource rB = new LockableResource("B", 2);
        var processor = createProcessor();
        for (int i = 1; i <= ITERATIONS; i++) {
            var t1 = new Thread(() -> doProcess(processor, rA, rB));
            var t2 = new Thread(() -> doProcess(processor, rB, rA));
            t1.start();
            t2.start();
            ThreadJoinUtils.safeJoin(t1, t2);
        }
    }

    private static LockableResourceProcessor createProcessor() {
        return switch (SCENARIO) {
            case WRONG -> new WrongLockableResourceProcessor();
            case ORDERED -> new OrderedLockableResourceProcessor();
            case TRY_LOCK -> new TryLockLockableResourceProcessor();
        };
    }

    private static void doProcess(LockableResourceProcessor processor, LockableResource rA, LockableResource rB) {
        try {
            processor.processResources(rA, rB);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}