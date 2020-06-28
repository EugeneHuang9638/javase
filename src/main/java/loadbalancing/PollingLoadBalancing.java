package loadbalancing;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * 轮询负载均衡算法
 *   轮询的含义就是一个一个挨个执行，
 *   当一轮执行完毕后，就从头继续执行
 *
 *
 *   ---> 最容易想到的就是一个环形链表。
 *   每次请求就拿到当前node的下一个
 *
 */
public class PollingLoadBalancing {


    static Node root = new Node("192.168.1.101");

    static Logger logger = Logger.getLogger(PollingLoadBalancing.class.getName());

    static {
        Node node2 = new Node("192.168.1.102");
        Node node3 = new Node("192.168.1.103");
        Node node4 = new Node("192.168.1.104");

        root.next = node2;
        node2.next = node3;
        node3.next = node4;
        node4.next = root;
    }

    public static void main(String[] args) throws InterruptedException {
        Node tmp = root;

        while (tmp != null) {
            logger.info("当前轮询的服务器IP: " + tmp.val);
            TimeUnit.SECONDS.sleep(1);
            tmp = tmp.next;
        }
    }

}

class Node {
    String val;
    Node next;

    public Node(String val) {
        this.val = val;
    }
}