package ru.korablev.secondmodule.prodconspract;

import lombok.Getter;
import ru.korablev.util.BusyCpuUtil;
import ru.korablev.util.Logger;
import ru.korablev.util.ThreadJoinUtils;
import ru.korablev.util.ThreadSleepUtil;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public class SimpleArrayBlockingQueue<T> {

    private final Object[] array;

    // количество элементов в очереди
    @Getter
    private int size = 0;

    // индекс первого элемента в очереди
    private int head = 0;

    // индекс последнего элемента в очереди
    private int tail = 0;


    public SimpleArrayBlockingQueue(int capacity) {
        this.array = new Object[capacity];
    }

    public synchronized T take(
            long timeout,
            TimeUnit unit
    ) throws InterruptedException, TimeoutException {
        awaitConditionWithTimeout(unit, timeout, this::isEmpty);
        T element = dequeueElement();
        this.notifyAll();
        return element;
    }

    public synchronized void put(T element,
                                 long timeout,
                                 TimeUnit unit
    ) throws InterruptedException, TimeoutException {
        Objects.requireNonNull(element, "Element is null");
        awaitConditionWithTimeout(unit, timeout, this::isFull);
        enqueue(element);
        this.notifyAll();
    }

    private void awaitConditionWithTimeout(
            TimeUnit unit,
            long timeout,
            BooleanSupplier condition
    ) throws InterruptedException, TimeoutException {

        long timeoutNanos = unit.toNanos(timeout);
        long deadline = System.nanoTime() + timeoutNanos;

        while (condition.getAsBoolean()) {
            long remainingNanos = deadline - System.nanoTime();

            if (remainingNanos <= 0) {
                throw new TimeoutException("Time elapsed");
            }

            TimeUnit.NANOSECONDS.timedWait(this, remainingNanos);
        }
    }

    public synchronized boolean isEmpty() {
        return size == 0;
    }

    public synchronized boolean isFull() {
        return size == array.length;
    }

    @SuppressWarnings("unchecked")
    private T dequeueElement() {
        var element = (T) array[head];
        array[head] = null;
        size--;
        head = (head + 1) % array.length;
        return element;
    }

    private void enqueue(T element) {
        array[tail] = element;
        size++;
        tail = (tail + 1) % array.length;
    }

    static void main() throws InterruptedException, TimeoutException {
        var queue = new SimpleArrayBlockingQueue<Integer>(10);
        Integer poisonPill = Integer.MIN_VALUE;

        var producer1 = new Thread(() -> produce(queue, 20),
                "producer-1");
        var producer2 = new Thread(() -> produce(queue, 15),
                "producer-2");
        var consumer = new Thread(() -> consume(queue, poisonPill));

        producer1.start();
        producer2.start();
        consumer.start();

        ThreadJoinUtils.safeJoin(producer1, producer2);
        queue.put(poisonPill, 20, TimeUnit.SECONDS);
        ThreadJoinUtils.safeJoin(consumer);
    }

    public static void produce(
            SimpleArrayBlockingQueue<Integer> queue,
            int countToProduce) {
        for (int i = 0; i < countToProduce; i++) {
            var element = i * i;
            try {
                queue.put(element, 1000, TimeUnit.MILLISECONDS);
                Logger.log("Element pushed to queue: %s", element);
                ThreadSleepUtil.safeSleepWithoutThrow(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Logger.log("Producer interrupted");
                break;
            } catch (TimeoutException e) {
                Logger.log("Producer timeout");
                break;
            }
        }
        Logger.log("Producer stopped");
    }

    public static void consume(
            SimpleArrayBlockingQueue<Integer> queue,
            Integer poisonPill
    ) {
        while (true) {
            Integer element = null;
            try {
                element = queue.take(1000, TimeUnit.MILLISECONDS);
                if (element.equals(poisonPill)) {
                    Logger.log("Consumer stopped");
                    break;
                }
                Logger.log("Element consumed from queue: %s", element);
                BusyCpuUtil.spinOnCpuMillis(500);
                Logger.log("Element from queue processed: %s", element);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Logger.log("Consumer interrupted");
                break;
            } catch (TimeoutException e) {
                Logger.log("Consumer timeout");
                break;
            }
        }
    }
}
