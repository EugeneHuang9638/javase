package leetcode;

import leetcode.model.ListNode;

/**
 * 给定一个链表: 返回它反转的链表
 * eg:
 * 输入: 1 -> 2 -> 3 -> 4 -> 5 -> null
 * 返回: 5 -> 4 -> 3 -> 2 -> 1 -> null
 *
 * 链表结构：
 * @see ListNode
 *
 *
 */
public class ReverseListNode2 {


    /**
     * @param head 头节点
     * @return 返回反转的链表
     */
    public static ListNode reverse(ListNode head) {
        // 链表为null的情况
        if (head == null) {
            return null;
        }

        // 链表为一个节点的情况
        if (head.next == null) {
            return head;
        }

        // 链表大于一个节点的情况
        // 初始化一个哨兵节点
        ListNode node = new ListNode(0);
        // 哨兵 -> head
        node.next = new ListNode(head.val);
        while (head.next != null) {
            // 一开始的写法，没有初始化新节点，导致链表被我搞断了
//            ListNode currentNode = head.next;
//            currentNode.next = node.next;
//            node.next = currentNode;
//
//            head = currentNode;
//----------------------------

            ListNode currentNode = head.next;
            ListNode inner = new ListNode(currentNode.val);
            inner.next = node.next;
            node.next = inner;

            head = currentNode;
        }

        return node.next;
    }

    public static void main(String[] args) {
        ListNode listNode5 = new ListNode(5);
        ListNode listNode4 = new ListNode(4);
        ListNode listNode3 = new ListNode(3);
        ListNode listNode2 = new ListNode(2);
        ListNode listNode1 = new ListNode(1);

        listNode1.next = listNode2;
        listNode2.next = listNode3;
        listNode3.next = listNode4;
        listNode4.next = listNode5;

        System.out.println(reverse(listNode1));
    }


}
