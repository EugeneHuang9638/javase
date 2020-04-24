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

## jdk1.7 ConcurrentHashMap源码

* 构造方法

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
      // sshift --->  存储的是ssize的2的次方幂的数字，比如16 = 2的4次方，所以sshit = 4
      while (ssize < concurrencyLevel) {
          ++sshift;
          ssize <<= 1;  // 左移1位，乘以2的一次方
      }
      // 28 ---> 这个变量是计算segment数组下标时用的
      // 因为hash值是一个32位的int类型数字，这里用32的原因就是
      // 后面会使用hash的高4位与segmentMask做&运算
      this.segmentShift = 32 - sshift;
      
      // segment数组长度减一, 猜测是为了计算index时用的
      this.segmentMask = ssize - 1;
      
      // 校验ConcurrentHashMap中的HashEntry的个数是否比1 << 30大
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
      // 对于上述的情况，假如initialCapacity = 33, ssize = 16
      // 那么算出来的c就会等于3. 但是呢，数组等于3，这样不好，因为最后在对计算index
      // 时，无法保证index分布均匀，在hashMap中，一般的容量都是2的幂方次的数
      // 所以此时，还要对cap进行处理，来获取大于c的2的幂次方的数
      // 比如上述情况下的c = 3，所需要找出比3大的2的幂次方的数字
      // 于是会执行下面一段代码，在c = 3的情况下，
      // cap执行完代码2 << 1 后，会变成4
      int cap = MIN_SEGMENT_TABLE_CAPACITY;
      while (cap < c)
          cap <<= 1;
  
      // create segments and segments[0]
      // 创建一个Segment对象，并将里面的HashEntry的扩容值给算出来了
      // 通过内部也维护了上述算出来的长度为cap的HashEntry数组
      Segment<K,V> s0 =
          new Segment<K,V>(loadFactor, (int)(cap * loadFactor),
                           (HashEntry<K,V>[])new HashEntry[cap]);
      
      // ss为放在table中的第一个segment数组, 长度为传入的concurrencyLevel
      Segment<K,V>[] ss = (Segment<K,V>[])new Segment[ssize];
      
      // 使用UNSAFE类操作数组， SBASE为Segment的offset
      // 在静态块中对SBASE进行了赋值
      // SBASE = UNSAFE.arrayBaseOffset(sc);   --> 指定位置
      // 所以这段代码是将so放在了SBASE的位置上
      UNSAFE.putOrderedObject(ss, SBASE, s0); // ordered write of segments[0]
      this.segments = ss;
  }
  ```

* put方法

  ```java
  public V put(K key, V value) {
      Segment<K,V> s;
      // ConcurrentHashMap的value不能为null --> 其实key也不能为null, 
      if (value == null)
          throw new NullPointerException();
      // 内部key并没有做null处理
      int hash = hash(key);
      
      // 这个j就是要put进来的对象存放在segment数组的下标
      // 其中segmentMask在构造方法里就处理过了，它的值为segment数组的长度
      // hash >>> segmentShift 是无符号右移，高位全部补0
      // 在构造方法中总结过了, segmentShift为 32 - segment长度的2的幂次方的数字(eg: segment的长度为16，那么此数字就是4)，所以segmentShift = 28
      // 因为hash是一个int类型的数字，所以会执行如下操作
      // hash: 01000011 01000011 01000011 01000011   ===>  假设是一个任意hash值
      // 当hash >>> 28后会变成如下:
      // hash >>> 28: 00000000 00000000 00000000 00000100
      // 所以可以看到最终j的值取决于hash >>> 28操作的后四位
      // 即hash的高四位，因为segmentMask为15
      // 所以(hash >>> segmentShift) & segmentMask的结果为：
      // hash        00000000 00000000 00000000 00000100
      // &
      // segmentMask 00000000 00000000 00000000 00001111
      // j的结果: 00000000 00000000 00000000 00000100 = 4
     	// 由此可以得出segment数组默认为16的情况下, 
      // 新put进来的元素放在segment的index的值取决于hash的高四位
      int j = (hash >>> segmentShift) & segmentMask;
      
      // 使用UNSAFE在segment数组中拿第(j << SSHIFT) + SBASE位置上的元素
      // 获取segments中(j << SSHIFT) + SBASE)位置上的元素
      /**
       详细解释下使用UNSAFE获取数组指定元素的逻辑
       1. 获取UNSAFE对象，这里写的是伪代码
       Unsafe unsafe = getUnsafe();
       2. 获取数组中存储的对象的对象头大小， 数组类型，默认为4
       ns = unsafe.arrayIndexScale(Object[].class);
       3. 获取数组中第一个元素的起始位置, 数组类型，默认为16
       base = unsafe.arrayBaseOffset(String[].class);
       4. 获取下标为4的元素
       unsafe.getObject(arr, base + 3 * ns)  ==> 获取的是数组中下标为4的元素
  
       // ConcurrentHashMap中
       1. 同理，获取对象头信息
       int SBASE = UNSAFE.arrayBaseOffset(Segment[].class);
       2. (j << SSHIFT) + SBASE 根据SSHIFT和SBASE的获取逻辑，将变形为如下代码:
       SBASE + (j << (31 - Integer.numberOfLeadingZeros(UNSAFE.arrayIndexScale(Segment[].class))))
       UNSAFE.arrayIndexScale(Segment[].class)获取的是对象类型，所以返回的值默认为4
       而4的二进制为 00000000 00000000 00000000 00000100
       而Integer.numberOfLeadingZeros(4) = 29
       最终变形为:
       SBASE + (j << (31 - 29)) = SBASE + j * 2的平方 = SBASE + j * 4
       所以找到的是第五个位置
      **/
      if ((s = (Segment<K,V>)UNSAFE.getObject          // nonvolatile; recheck
           (segments, (j << SSHIFT) + SBASE)) == null) //  in ensureSegment
          // 如果获取的为null，则创建一个新的
          s = ensureSegment(j);
      
      // 最后在将put进来的对象放入至HashEntry中
      return s.put(key, hash, value, false);
  }
  ```

* 创建新的segment， `ensureSegment`方法

  ```java
  private Segment<K,V> ensureSegment(int k) {
      // 假设在外部put了一个元素到ConcurrentHashMap，
      // 此时要定位这个元素放在哪个segment数组的下表中
      // 这个k就是下标，因为此下标上没有segment对象，所以需要创建一个
      // 但是这个k并不是使用UNSAFE从数组中获取对象的下标,
      // 这个k是使用hash算法后的下标
      final Segment<K,V>[] ss = this.segments;
      
      // 因为并发的情况，需要使用UNSAFE去操作数组，于是要计算
      // UNSAFE操作数组时的下标
      long u = (k << SSHIFT) + SBASE; // raw offset
      
      // 要新创建的segment对象
      Segment<K,V> seg;
  
      // 使用UNSAFE校验，segment中下标为k中是否有对象
      if ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u)) == null) {
          // 使用构造方法中创建的segment对象
          // 因为此对象中存储了当前ConcurrentHashMap中每个segment内部的HashEntry数组的信息
          Segment<K,V> proto = ss[0]; // use segment 0 as prototype
          // 默认为2
          int cap = proto.table.length;
          // 记载因子
          float lf = proto.loadFactor;
          // 扩容的阈值
          int threshold = (int)(cap * lf);
          // 创建了一个HashEntry数组
          HashEntry<K,V>[] tab = (HashEntry<K,V>[])new HashEntry[cap];
          
          // 再次校验，segment中下标为k的地方有没有对象
          // 因为有可能在高并发的情况下，第一个线程走完了上述的第一个校验
          // 但是第二个线程可能已经把新建segment的流程都走完了。
          // 所以在关键的地方又校验了一遍
          if ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u))
              == null) { // recheck
              // 新建一个segment对象
              Segment<K,V> s = new Segment<K,V>(lf, threshold, tab);
              // 这里使用了自旋锁
              // 终止自旋的条件有两个
              // 1. segment中index=k的位置上已经有segment对象了
  		   // 2. 将新建的segment对象添加成功
              // 在这里可能发生如下并发的情况:
              // 若第一个线程在while条件中校验通过了，此时进行cas操作时，操作系统发现
              // 指定位置上的值不为null(被其他线程给cas成功了)，此时为false,
              // 于是再走while条件，发现已经不为null了，于是自旋结束
              while ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u))
                     == null) {
                  if (UNSAFE.compareAndSwapObject(ss, u, null, seg = s))
                      break;
              }
          }
      }
      return seg;
  }
  ```

* HashEntry的put方法

  ```java
  final V put(K key, int hash, V value, boolean onlyIfAbsent) {
      // 因为HashEntry继承了ReentrantLock类，所以它自己是一把锁
      // 在高并发情况下，如果tryLock()方法返回的true，即加锁成功
      // 可以放心的处理后面的逻辑了。如果加锁失败，又会采用自旋的
      // 策略进行加锁
      HashEntry<K,V> node = tryLock() ? null :
      scanAndLockForPut(key, hash, value);
      V oldValue;
      try {
          // 获取内部维护的数组
          HashEntry<K,V>[] tab = table;
          // 使用获取index的算法，与HashMap一致
          int index = (tab.length - 1) & hash;
          // 使用cas获取指定位置的元素，校验有没有值
          HashEntry<K,V> first = entryAt(tab, index);
          
          // 一个死循环
          for (HashEntry<K,V> e = first;;) {
              // 如果指定位置上有值
              // 和hashMap一致，key相同则覆盖，并返回原来的值
              // 若key不相同，则继续遍历，所以 if (e != null)
              // 的分支处理的逻辑是key相同的情况
              if (e != null) {
                  K k;
                  if ((k = e.key) == key ||
                      (e.hash == hash && key.equals(k))) {
                      oldValue = e.value;
                      if (!onlyIfAbsent) {
                          e.value = value;
                          ++modCount;
                      }
                      break;
                  }
                  e = e.next;
              }
              else {
                  // TODO node ！= null的情况为获取锁失败，即在高并发的情况下，待总结
                  if (node != null)
                      node.setNext(first);
                  else
                      // 如果指定位置上没有值，则新new一个
                      node = new HashEntry<K,V>(hash, key, value, first);
                  // 将ConcurrentHashMap的数量 + 1
                  int c = count + 1;
                  // 判断是否需要扩容
                  if (c > threshold && tab.length < MAXIMUM_CAPACITY)
                      rehash(node);
                  else
                      // 使用UNSAFE将新增的元素放在指定位置上
                      setEntryAt(tab, index, node);
                  ++modCount;
                  count = c;
                  oldValue = null;
                  break;
              }
          }
      } finally {
          // 解锁，完成put操作
          unlock();
      }
      return oldValue;
  }
  ```

  