package leetcode;

import leetcode.model.ListNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 给定一个链表: 返回它反转的链表
 * eg:
 * 输入: 1 -> 2 -> 3 -> 4 -> 5 -> null
 * 返回: 5 -> 4 -> 3 -> 2 -> 1 -> null
 *
 *
 */
public class ReverseListNode {

    /**
     * 时间复杂度为:
     * O(n) + O(n - 1) = O(2*n -1) = O(n)
     *
     * 但是很复杂，代码有点多，咱们优化下
     *
     * @param listNode
     * @return
     */
    public static ListNode reverseList(ListNode listNode) {
        List<ListNode> listNodes = new ArrayList<>();
        while (listNode.next != null) {
            listNodes.add(listNode);
            listNode = listNode.next;
        }

        ListNode newListNode = listNodes.get(listNodes.size() - 1);
        ListNode listNodeTool = newListNode;
        for (int i = listNodes.size() - 2; i >= 0 ; i--) {
            listNodeTool.next = listNodes.get(i);

            listNodeTool = listNodeTool.next;
        }

        return newListNode;
    }

    /**
     * 优化后，基本上使用的是修改引用的方式
     *
     * 大概逻辑为如下:
     * 遍历node的时候肯定是按照1，2，3，4，5的顺序来执行的。
     * 而链表的插入逻辑，就是拿到原始的链表节点，并设置原始节点的next为新new出来的节点
     *
     * 所以我们可以定义一个空链表作为新链表，
     * 当开始遍历传入的链表时，第一次肯定是val为1的节点
     * 此时我们new出这个新链表，它的值就是当前遍历节点的值
     * 随后，我们在每一次便利时都new出一个新节点，
     * 同时这个新节点的next节点就为上述的新链表 ====> 这里就是反转的过程
     * 最后再将这个新节点复制给新链表，即下面
     * 这段代码:
     * ListNode inner = new ListNode(listNode.val);
     * inner.next = newNode;
     * newNode = inner;
     *
     *
     *
     * @param head
     * @return
     */
    public static ListNode reverseList2(ListNode head) {
        ListNode newListNode = null;

        while (head != null) {
            if (newListNode == null) {
                newListNode = new ListNode(head.val);
            } else {
                ListNode inner = new ListNode(head.val);
                inner.next = newListNode;

                newListNode = inner;
            }

            head = head.next;
        }

        return newListNode;
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

        System.out.println(reverseList2(listNode1));
    }

}
