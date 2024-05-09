package leetcode.lru;

/**
 * @author muyang
 * @create 2024/5/9 19:13
 */
public class LRUCache {

    /**
     * 缓存的容量，超过这个值时，就要开始删除数据了
     */
    int capacity;

    /**
     * 当前链表的长度
     */
    int count;

    /**
     * 头节点
     */
    Node head;
    /**
     * 尾节点
     */
    Node tail;

    /**
     * 初始化链表长度
     */
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.count = 0;
    }

    public void addElement(Node node) {
        if (head == null) {
            this.head = node;
            this.tail = node;
            count++;
            return;
        } else {
            // 头结点不为空

            // 容量满了
            if (count == capacity) {
                // 删除尾节点

                // 上一个节点为尾节点
                this.tail = this.tail.prev;
                // 为节点的下一个几点为null
                this.tail.next = null;
                count--;
            }

            // 容量未满
            // 把新加的节点放在头部
            Node tmp = this.head;
            this.head.next = tmp;
            this.head = node;
            node.next = tmp;
        }
    }

    public static void main(String[] args) {
        LRUCache cache = new LRUCache(1);
        cache.addElement(new Node(1));
        cache.addElement(new Node(2));
    }



    /**
     * 节点内容
     * @value 节点的值
     * @prev 上一个节点的值
     * @next 下一个节点的值
     */
    public static class Node {
        Object value;
        Node next;
        Node prev;

        public Node(Object value) {
            this.value = value;
        }
    }


}
