package leetcode;


import leetcode.model.ListNode;

import java.util.HashSet;
import java.util.Set;

/**
 * 给定一个链表，判断链表中是否有环。
 *
 * 为了表示给定链表中的环，我们使用整数 pos 来表示链表尾连接到链表中的位置（索引从 0 开始）。 如果 pos 是 -1，则在该链表中没有环。
 *
 */
public class HasCycle2 {


    public static boolean hasCycle(ListNode head) {
        boolean result = false;
        // 空链表、一个元素、两个元素的链表是不可能有环的
        if (head == null || head.next == null || head.next.next == null) {
            return result;
        }

        // 同一个起点开始走
        ListNode fast = head;
        ListNode slow = head;

        while (head.next != null) {
            fast = fast.next.next;
            slow = slow.next;
            if (fast == slow) {
                result = true;
                break;
            }
            head = head.next;
        }

        return result;
    }

    public static void main(String[] args) {
        ListNode node5 = new ListNode(5);
        ListNode node4 = new ListNode(4);
        ListNode node3 = new ListNode(3);
        ListNode node2 = new ListNode(2);
        ListNode node1 = new ListNode(1);
        node4.next = node5;
        node3.next = node4;
        node2.next = node3;
        node1.next = node2;

        // 创建环:
        node5.next = node3;

        System.out.println(hasCycle(node1));
    }
}
