package com.eugene.basic.concurrency.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class JMHWithTwoHighConcurrentCollections {

    // 包含少量元素
    CopyOnWriteArrayList smallCopyOnWriteList = new CopyOnWriteArrayList();
    ConcurrentLinkedQueue smallConcurrentLinkedQueue = new ConcurrentLinkedQueue();

    // 包含大量元素
    CopyOnWriteArrayList bigCopyOnWriteList = new CopyOnWriteArrayList();
    ConcurrentLinkedQueue bigConcurrentLinkedQueue = new ConcurrentLinkedQueue();


    @Setup
    public void initializeCollections() {
        for (int i = 0; i < 10; i++) {
            smallConcurrentLinkedQueue.add(new Object());
            smallCopyOnWriteList.add(new Object());
        }

        for (int i = 0; i < 1000; i++) {
            bigConcurrentLinkedQueue.add(new Object());
            bigCopyOnWriteList.add(new Object());
        }
    }

    @Benchmark
    public void smallCopyOnWriteGet() {
        smallCopyOnWriteList.get(0);
    }

    @Benchmark
    public void smallCopyOnWriteSize() {
        smallCopyOnWriteList.size();
    }

    @Benchmark
    public void smallConcurrentLinkedQueueGet() {
        smallConcurrentLinkedQueue.peek();
    }

    @Benchmark
    public void smallConcurrentLinkedQueueSize() {
        smallConcurrentLinkedQueue.size();
    }

    @Benchmark
    public void bigCopyOnWriteGet() {
        bigCopyOnWriteList.get(0);
    }

    @Benchmark
    public void bigCopyOnWriteSize() {
        bigCopyOnWriteList.size();
    }

    @Benchmark
    public void bigConcurrentLinkedQueueGet() {
        bigConcurrentLinkedQueue.peek();
    }

    @Benchmark
    public void bigConcurrentLinkedQueueSize() {
        bigConcurrentLinkedQueue.size();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHWithTwoHighConcurrentCollections.class.getSimpleName())
                .threads(6)
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.MICROSECONDS)
                .forks(1)
                .warmupIterations(10)
                .build();
        new Runner(opt).run();
    }

}
