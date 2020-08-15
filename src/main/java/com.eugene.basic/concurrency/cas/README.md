### CAS相关总结

#### 一、AutomicInterge为什么用CAS而不用synchronized

* 因为cas比较轻量，是一种乐观锁，虽然比synchronized轻量，但是在并发特别高的情况下不建议使用cas，因为操作

  cpu的频率比synchronized高，进而影响cpu的运行效率。

### 二、什么叫CAS

* **compare and set**，比较并替换，使用的是unsafe类，由unsafe类来操作底层cpu，保证一次只有一个线程来给cpu发送指令

#### 三、CAS的ABA问题描述即解决

* CAS会出现ABA问题，也就是说线程A第一次拿到的值和第二次拿到的值有可能是一样的，但是中途可能会被线程B和

  线程C处理过，线程B把0改成了1，线程C又把1改成了0。要解决这个问题，可以使用juc下的拥有版本号的相关原子

  类，比如**AtomicStampedReference**类