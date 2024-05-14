package leetcode;

import leetcode.model.ListNode;

/**
 * 返回中间指针
 * 输入：[1, 2, 3, 4, 5]
 * 输出：此链表的中间节点是 3。
 *
 * 输入：[1, 2, 3, 4, 5, 6]
 * 输出：此链表的中间节点有两个（3 和 4），返回第二个节点 4。
 */
public class ReturnMiddleNode {

    /**
     * 思路：依然还是快慢指针，快指针走两步，慢指针走一步。
     * 推演一下：就能知道了。
     * @return
     */
    public static ListNode returnMiddle(ListNode head) {
        ListNode fast = head;
        ListNode slow = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }


    public static void main(String[] args) {
        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(1);
        node1.next = node2;

        ListNode node3 = new ListNode(1);
        node2.next = node3;

        ListNode node33 = new ListNode(2);
        node3.next = node33;

        ListNode node4 = new ListNode(2);
        node33.next = node4;

        ListNode node44 = new ListNode(3);
        node4.next = node44;

        ListNode node55 = new ListNode(4);
        node44.next = node55;

        ListNode listNode = returnMiddle(node1);
        System.out.println(1);
    }

}
