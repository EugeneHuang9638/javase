# 并发集合

## 阻塞式集合
  ```text
    这类集合包括添加和移除数据的方法, 当集合已满或为空时, 被调用的添加或者移除方法就不能立即被执行, 那么调用这个方法的线程将被阻塞,
    一直到该方法可以被成功执行。
  ```

## 非阻塞式集合
  ```text
    这类集合包括添加和移除数据的方法, 如果方法不能立即被执行, 则返回null或抛出异常, 但是调用这个方法的线程不会被阻塞
  ```

## HashMap底层数据结构
  ```text
    1. 数组 + 链表的数据结构,  在数组的每一个元素里面内嵌一个链表(存储链表头的指针), 链表中的每个元素包含三个部分, 分别是  key, value, next
    2. 在jdk 1.7之前, 当链表比较长的时候, 当要搜索具体一个值时, 需要一个一个的去搜索. 效率为o(链表的长度), 为了解决搜索慢的问题, 
       在jdk 1.8时引入了红黑树的数据结构. 当链表长度大于8且数组长度大于64的时候. 会将数据结构转换成红黑树
    3. 红黑树: 根节点和叶子节点都是黑色的, 
    4. jdk hashmap 桶(数组)的长度有一个扩容的机制(非常耗时, 当size达到factor(扩容因子)的百分之75的时候, 开始扩容, 扩容原来的2倍), 
  ```

## ConcurrentHashMap
  ```text
     1. 在1.7
     2. 在jdk 1.8版本时, 底层采用的数据结构式红黑树
     3. 解决并发操作集合的策略是: 使用分段锁, 它会将桶里的某个部分分段, 只对某个段进行加锁
        分段策略: 
  ```

## jdk1.7 ConcurrentHashMap构造方法

```java
/**
 * initialCapacity: ConcurrentHashMap中存储HashEntry的总个数
 * loadFactor: 记载因子
 * concurrencyLevel: Segment的个数 ---> ConcurrentHashMap中维护的数组长度
**/
public ConcurrentHashMap(int initialCapacity,
                         float loadFactor, int concurrencyLevel) {
    if (!(loadFactor > 0) || initialCapacity < 0 || concurrencyLevel <= 0)
        throw new IllegalArgumentException();
    
    // 当传入的segment数量个数大于2的16次方，则使用2的16次方
    // 所以segment最大数量为2的16次方
    // MAX_SEGMENTS = 2的16次方
    if (concurrencyLevel > MAX_SEGMENTS)
        concurrencyLevel = MAX_SEGMENTS;
    // Find power-of-two sizes best matching arguments
    int sshift = 0;
    int ssize = 1;
    
    // concurrencyLevel默认等于16
    // 默认情况下
    //               sshift     ssize
    // 1 < 16  =>      1          2
    // 2 < 16  =>      2          4
    // 4 < 16  =>      3          8
    // 8 < 16  =>      4          16
    // 16 < 16 停止循环
    
    // 由上可知，
    // ssize存储的值与传入的concurrencyLevel相同，即segment的个数(内部维护数组的长度)
    // sshift 暂时不知道是干嘛的
    while (ssize < concurrencyLevel) {
        ++sshift;
        ssize <<= 1;  // 左移1位，乘以2的一次方
    }
    this.segmentShift = 32 - sshift;
    
    // segment数组长度减一, 猜测是为了计算index时用的
    this.segmentMask = ssize - 1;
    
    // 校验ConcurrentHashMap中的HashEntry的个数 1 << 30 
    if (initialCapacity > MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    
    // 来确定每个segment中的hashEntry数组的长度
    // initialCapacity为ConcurrentHashMap中HashEntry的个数
    // ssize为ConcurrentHashMap中segment的个数, 
    // initialCapacity / ssize  ==> 能得到一个segment中的HashEntry数组的长度
    // 若initialCapacity = 默认的16, 那么1为segment中的HashEntry数组的长度
    // 此时 1 * 16 < 16, 所以c不需要自增，
    // 若initialCapacity = 33, 而ssize = 16, 此时c = 2
    // 而 1 * 16 < initialCapacity = 33 所以此时c会加1
    // 所以这一段代码的作用就是向上取整
    // 从这也能看出, 因为Segment中维护的HashEntry数组的长度最小为2
    // 所以至少会有 2 * ssize(concurrencyLevel)个HashEntry
    // 若传入的initialCapacity > 2 * ssize
    // 则必须要增加每个segment中HashEntry数组的长度
    // eg: 上述所说的: initialCapacity = 33, ssize = 16的情况
    // 33 > 2 * 16, 此时放不下33个hashEntry，所以要将
    // segment中HashEntry中的数组长度加大
    int c = initialCapacity / ssize;
    if (c * ssize < initialCapacity)
        ++c;
    
    // cap = MIN_SEGMENT_TABLE_CAPACITY = 2, 即HashEntry数组的最小长度，
    // 所以就算算出来的c = 1，但是最后默认长度也会变成2
    int cap = MIN_SEGMENT_TABLE_CAPACITY;
    while (cap < c)
        cap <<= 1;

    // create segments and segments[0]
    // 
    Segment<K,V> s0 =
        new Segment<K,V>(loadFactor, (int)(cap * loadFactor),
                         (HashEntry<K,V>[])new HashEntry[cap]);
    
    // ss为放在table中的第一个segment数组, 长度为传入的concurrencyLevel
    Segment<K,V>[] ss = (Segment<K,V>[])new Segment[ssize];
    
    // 使用UNSAFE类操作数组
    UNSAFE.putOrderedObject(ss, SBASE, s0); // ordered write of segments[0]
    this.segments = ss;
}
```

