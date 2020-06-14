package leetcode;

import leetcode.model.ListNode;

import java.util.HashSet;
import java.util.Set;

/**
 * 给定一个排序链表，删除所有含有重复数字的节点，只保留原始链表中 没有重复出现 的数字。
 *
 * 示例1:
 * 输入: 1->2->3->3->4->4->5
 * 输出: 1->2->5
 *
 * 示例2:
 * 输入: 1->1->1->2->3
 * 输出: 2->3
 *
 * 方法一：
 *   使用一个set记录去重的元素，遍历一个节点时，把元素放入set中，如果当前进入的元素是在set中
 *   ，那么则把root置为上一个元素
 */
public class DeleteDuplicates {


    public static ListNode deleteDupl(ListNode root) {
        if (root == null) {
            return null;
        }

        if (root.next == null) {
            return root;
        }

        ListNode tmp = root;
        ListNode head = new ListNode(-1);
        ListNode preNode = head;

        int duplCount = 0;

        while (tmp != null) {
            if (head.val != tmp.val) {
                if (duplCount > 0) {
                    head = preNode;
                    head.next = tmp;
                    duplCount = 0;
                } else {
                    head.next = tmp;
                    preNode = head;
                    head = tmp;
                }
            } else {
                duplCount++;
            }

            tmp = tmp.next;
        }

        return head.next;
    }

    public static void main(String[] args) {
        ListNode node1 = new ListNode(1);
        ListNode node2 = new ListNode(2);
        node1.next = node2;

        ListNode node3 = new ListNode(3);
        node2.next = node3;

        ListNode node33 = new ListNode(3);
        node3.next = node33;

        ListNode node4 = new ListNode(4);
        node33.next = node4;

        ListNode node44 = new ListNode(4);
        node4.next = node44;

        ListNode node55 = new ListNode(5);
        node44.next = node55;

        System.out.println(deleteDupl(node1));
    }
}
