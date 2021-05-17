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
 * JMH hello world程序
 *
 * @Benchmark 注解表示此方法是一个被度量的单位（类似于junit）
 * 在main函数中，首先对测试用例进行配置，使用Builder模式配置测试，将配置参数存入Options对象，并
 * 使用Options对象构造Runner启动测试
 *
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class JMHHelloWorld1 {

    @Benchmark
    public void wellHelloThere() {
        //
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(JMHHelloWorld1.class.getSimpleName())
                .forks(1).build();

        new Runner(opt).run();
    }

}
