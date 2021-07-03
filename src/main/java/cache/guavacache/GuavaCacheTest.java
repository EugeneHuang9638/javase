package cache.guavacache;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 缓存是放在堆内存的，如果数据量非常大的话，就会造成内存溢出（即使堆会有jvm做垃圾回收，
 * 但是缓存中的数据通常都不是垃圾数据，因此，会一直占用堆内存大小，如果不好好处理的话，会造成堆内存溢出）
 *
 * 因此：我们在使用对内存的时候，要额外考虑下，会不会出现缓存超级大的情况，进而造成堆内存溢出的情况。
 * 通常，我们会把最热的数据放在堆缓存中去。
 *
 *
 *
 */
public class GuavaCacheTest {

    private final static Cache cache = CacheBuilder.newBuilder()
            // 设置cache的初始大小为10，要合理设置该值
            .initialCapacity(10)
            // 设置并发数为5，即同一时间可以有5个线程往cache执行写入操作数值设置越高并发能力越强
            .concurrencyLevel(5)
            // 设置cache中的数据在写入之后的存活时间为5秒
            .expireAfterWrite(5, TimeUnit.SECONDS)
            // 缓存这保存的最大key数量
            .maximumSize(10000)
            // 构建cache实例
            .build();


    public static void main(String[] args) throws ExecutionException, InterruptedException {

        /**
         * 我们通常是先从缓存中获取，如果缓存不存在则从数据库中取，然后再放到缓存中
         * 这个操作可以用cache.get(key, callable)的api实现，
         * 第二个回调函数就是当缓存中key不存在时，则从callable中去
         */
        for (int i = 0; i < 11; i++) {
            Object avengerEug = cache.get("avengerEug", new Callable() {
                @Override
                public Object call() throws Exception {
                    System.out.println("模拟从数据库中取");
                    return 1L;
                }
            });

            System.out.println("avengerEug: " + avengerEug);
            Thread.sleep(1000);
        }

    }

}
