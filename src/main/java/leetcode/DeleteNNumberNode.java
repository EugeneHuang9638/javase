package leetcode;

import leetcode.model.ListNode;

/**
 * 删除倒数第n个节点
 * @author muyang
 * @create 2024/5/10 21:22
 */
public class DeleteNNumberNode {


    /**
     * 核心：两个快慢指针，相差n个节点。让快指针移动到队尾后，停止操作。此时的慢指针就是在第n-1个位置
     * @param head
     * @param number
     */
    public static ListNode deleteByNumber(ListNode head, int number) {
        // 添加一个哑节点（dummy node），指向头节点
        // 这样可以更方便地处理头节点的删除问题
        ListNode dummy = new ListNode(0);
        dummy.next = head;

        ListNode slow = dummy;
        ListNode fast = dummy;

        // fast 指针先提前走 number + 1 步（为什么是+1？要确保fast节点指向末尾节点null时，slow指针刚好在n-1节点上）
        for (int i = 0; i < number + 1 && fast != null; i++) {
            fast = fast.next;
        }

        // 如果number大于链表长度，fast将为null，不执行删除操作
        if (fast == null) {
            return dummy.next;
        }

        // 移动 slow 和 fast，直到 fast 是最后一个节点
        // 这里是关键：一定是fast != null的情况。如果fast是最后一个，那fast指向的就是null了。此时slow节点的next就是倒数第n个节点
        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }

        // 删除操作，此时 slow 指向待删除节点的前一个节点
        slow.next = slow.next.next;

        // 返回新的链表头节点，即dummy的下一个节点
        return dummy.next;
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

        //System.out.println(deleteDuplForHash(node1));
        ListNode node = deleteByNumber(node1, 3);
        System.out.println(1);
    }


}
