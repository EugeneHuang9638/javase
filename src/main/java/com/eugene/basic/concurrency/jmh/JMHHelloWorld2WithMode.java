package com.eugene.basic.concurrency.jmh;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * 测试某个方法的吞吐量
 */
public class JMHHelloWorld2WithMode {

    @Benchmark
//    @BenchmarkMode(Mode.Throughput)  // 计算吞吐量
    @BenchmarkMode(Mode.SampleTime)  // 计算采样，能计算出50%、90%、99%、99.99%的请求耗时多少，
    @OutputTimeUnit(TimeUnit.SECONDS)
    public void measureThroughput() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHHelloWorld2WithMode.class.getSimpleName())
                .warmupIterations(5)
                .forks(1).build();

        new Runner(opt).run();
    }

}
