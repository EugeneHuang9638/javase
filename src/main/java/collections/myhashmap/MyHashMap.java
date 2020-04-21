package collections.myhashmap;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap原理： 数组 + 链表
 *
 * HashMap获取index的原理
 *
 *
 */
public class MyHashMap<K, V> {

    // 16
    private static final Integer DEFAULT_INITIAL_CAPACITY =  1 << 4;

    // 存放链表的数组
    private Entry<K, V>[] table;

    private int size;

    // 跟ArrayList源码差不多，每次put的时候新增一个
    // 这样带来的好处是，我不需要遍历数组中的所有链表
    // 本来链表遍历就慢。。。
    public int size() {
        return this.size;
    }

    private void initTable(int capacity) {
        table = new Entry[capacity];
    }

    // 真实容量
    public int sureCapacity;


    public MyHashMap() {
        this.sureCapacity = DEFAULT_INITIAL_CAPACITY;
        initTable(sureCapacity);
    }

    public MyHashMap(int capacity) {
        this.sureCapacity = capacity;
        initTable(sureCapacity);
    }


    /**
     * JDK中返回的是原来的值,
     * 因为hashMap不可重复的特性，若put进去的key是一致的话，
     * 后面的会覆盖前面的
     * @param k
     * @param v
     * @return
     */
    public Object put(K k, V v) {
        int targetIndex = getIndex(k);
        // 获取当前在index中的节点
        Entry<K, V> currentEntryForIndex = table[targetIndex];


        // 若key在链表中已经存在, 则覆盖，并返回
        for (Entry<K, V> entry = currentEntryForIndex; entry != null ; entry = entry.next) {
            if (entry.k.equals(k)) {
                // 拿到oldValue
                V oldValue = entry.v;
                // 覆盖value
                entry.v = v;

                // 为什么要return呢？ 因为没必要在链表中加值了呀，直接覆盖
                return oldValue;
            }
        }

        // 创建当前要放入数组中的Entry,
        // 因为在jdk1.7采用的是头插法，而hashMap中的entry内部没有维护上一个节点的属性
        // 所以需要将当前的节点放到新增的节点的next节点上
        // 然后将新增的节点放入到index中
        Entry<K, V> entryInserted = new Entry<>(k, v, currentEntryForIndex);

        table[targetIndex] = entryInserted;
        size++;

        return null;
    }

    public V get(K k) {
        int targetIndex = getIndex(k);

        Entry<K, V> currentEntryForIndex = table[targetIndex];

        traverseEntry(currentEntryForIndex);

        // 若key在链表中已经存在, 则覆盖，并返回
        for (Entry<K, V> entry = currentEntryForIndex; entry != null ; entry = entry.next) {
            if (entry.k.equals(k)) {
                // 为什么要return呢？ 因为没必要在链表中加值了呀，直接覆盖
                return entry.v;
            }
        }

        return null;
    }

    /**
     * 先获取到key的hashcode，
     * hashcode再对sureCapacity取余
     * @param k
     * @return
     */
    private Integer getIndex(K k) {
        int hashcode = k.hashCode();
        return hashcode % sureCapacity;
    }

    private static class Entry<K, V> {

        // key
        private K k;

        // value
        private V v;

        private Entry<K, V> next;

        public K getK() {
            return k;
        }

        public V getV() {
            return v;
        }

        public Entry<K, V> getNext() {
            return next;
        }

        public Entry(K k, V v, Entry<K, V> next) {
            this.k = k;
            this.v = v;
            this.next = next;
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "k=" + k +
                    ", v=" + v +
                    ", next=" + next +
                    '}';
        }
    }


    public void traverseEntry(Entry<K, V> entry) {
        // 若key在链表中已经存在, 则覆盖，并返回
        for (Entry<K, V> entryInner = entry; entryInner != null ; entryInner = entryInner.next) {
            System.out.println(entryInner);
        }
    }


    /**
     * HashMap一定要的初始化大小为什么一定要为16？
     *
     * 说一定要为16也不一定，但是只要保证第四位的值权威1就可以了 所以它可以是32， 64， 128等等
     * 因为在根据key来做hash时，需要对hash值做位移操作
     * h--indexFor()、h&(length-1)
     *
     *
     * jdk1.8
     *
     * 当size超过8时会变成红黑树，当size少于6时又会转化成链表
     *
     * jdk1.8 put方法采用的是尾插法
     *
     * hash时没有jdk1.7那么麻烦了，也就是hash的散列性没有那么高，虽然这会导致一些key一直hash到同一个地方
     * 但是没关系，因为链表长了，会变成红黑树，红黑树的查询效率比链表还是高的
     *
     *
     * resize方法
     * @param args
     */
    public static void main(String[] args) {
        /*MyHashMap<String, String> myHashMap = new MyHashMap<>();
        for (int i = 0; i < 20; i++) {
            // 对应的hashcode
            int hashcode = (i + "").hashCode();
            System.out.println(String.format("%s对应的hashcode%s, index为%s", i, hashcode, (hashcode % myHashMap.sureCapacity)));
            myHashMap.put(i + "", i + "");
        }

        // 可以对应查看下myHashMap的数据结构，0和11的key对应的index都为0，利用、
        // jdk1.7 的头插法特性，可以确认下，11是否在0的前面
        myHashMap.get("0");
        System.out.println(myHashMap.size());*/

        Map<String, Object> map = new HashMap();
        map.put(null, "111");
        System.out.println(111);
    }
}
