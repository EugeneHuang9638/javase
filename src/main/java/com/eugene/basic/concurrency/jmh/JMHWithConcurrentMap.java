package com.eugene.basic.concurrency.jmh;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 测试线程安全的两个map：Collections.synchronizedMap和concurrentHashMap的指定api(get和size api)的吞吐量
 *
 * 执行完启动类后，耐心等待，最终会输出每个@Benchmark的测评结果
 * 类似如下的结果：
 * Benchmark                                    Mode  Cnt           Score         Error  Units
 * JMHWithConcurrentMap.concurrentHashMapGet   thrpt   20   227639078.161 ±  410748.946  ops/s
 * JMHWithConcurrentMap.concurrentHashMapSize  thrpt   20  1129301142.656 ± 8665760.237  ops/s
 * JMHWithConcurrentMap.synchronizedMapGet     thrpt   20    55655543.582 ±  139013.678  ops/s
 * JMHWithConcurrentMap.synchronizedMapSize    thrpt   20    60011737.567 ±  196855.551  ops/s
 *
 * 测评结果显示：
 *   jdk1.8的concurrentHashMap在get和size方法都秒杀了collections.synchronizedMap、
 *
 *
 * 修改main，指定threads为2，多线程的情况下，再看下测评结果：
 * Benchmark                                    Mode  Cnt           Score          Error  Units
 * JMHWithConcurrentMap.concurrentHashMapGet   thrpt   20   433949557.931 ±  8905159.250  ops/s
 * JMHWithConcurrentMap.concurrentHashMapSize  thrpt   20  2217563016.895 ± 33125630.438  ops/s
 * JMHWithConcurrentMap.synchronizedMapGet     thrpt   20    28695140.705 ±   715076.884  ops/s
 * JMHWithConcurrentMap.synchronizedMapSize    thrpt   20    23166209.108 ±   780747.161  ops/s
 *
 * 讲道理，用了多线程后，执行速度应该会更快，但由于concurrentHashMap和Collections.synchronizedMap是
 * 线程安全的，因此它内部肯定会加锁。而concurrentHashMap内部加的是分段锁，粒度更小，肯定比
 * Collections.synchronizedMap效率更高
 *
 */
@State(Scope.Benchmark)
public class JMHWithConcurrentMap {

    static Map synchronizedMap = Collections.synchronizedMap(new HashMap<>(1024));
    static Map concurrentHashMap = new ConcurrentHashMap(1024);

    @Setup
    public void before() {
        for (int i = 0; i < 1000; i++) {
            String iString = String.valueOf(i);
            synchronizedMap.put(iString, iString);
            concurrentHashMap.put(iString, iString);
        }
    }

    @Benchmark
    public void synchronizedMapGet() {
        synchronizedMap.get("4");
    }

    @Benchmark
    public void concurrentHashMapGet() {
        concurrentHashMap.get("4");
    }

    @Benchmark
    public void synchronizedMapSize() {
        synchronizedMap.size();
    }

    @Benchmark
    public void concurrentHashMapSize() {
        concurrentHashMap.size();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHWithConcurrentMap.class.getSimpleName())
                .forks(1)
//                .threads(1)
                .threads(2)
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.SECONDS)
                .build();
        new Runner(opt).run();
    }

}
