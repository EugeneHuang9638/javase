package leetcode;

import leetcode.model.ListNode;

/**
 * 题目: 合并两个有序链表
 * 分析: 题目中已经标明了是两个有序的链表了
 * 所以每个链表肯定是按照升序或者降序排列的
 *
 * 将两个升序链表合并为一个新的升序链表并返回。新链表是通过拼接给定的两个链表的所有节点组成的。
 *
 * 实例:
 * 输入：1->2->4, 1->3->4
 * 输出：1->1->2->3->4->4
 *
 */
public class MergeTwoSortedLists {

    public static void main(String[] args) {
        // 构建两个链表
        ListNode l13 = new ListNode(4);
        ListNode l12 = new ListNode(2);
        ListNode l11 = new ListNode(1);
        l12.next = l13;
        l11.next = l12;

        ListNode l23 = new ListNode(4);
        ListNode l22 = new ListNode(3);
        ListNode l21 = new ListNode(1);
        l22.next = l23;
        l21.next = l22;

        //System.out.println(merge(l11, l21));
        System.out.println(iterate(l11, l21));
    }

    /**
     * 递规的基本思想:
     *   把一个大型复杂的问题层层转化为一个与原问题相似的规模较小的问题来求解
     *
     * 递归解决:
     *   因为两个链表的合并，在大范围而言，是两个链表合并，将大的链表放在小的链表后面
     *   而链表是由一个个节点组成的，所以可以拆解成若干个子问题, 即把每个节点进行合并，
     *   将大的节点放在小的节点后面
     *
     * 时间复杂度 o(m + n)  m, n为链表的长度
     * 空间复杂度 o(m + n)  因为递归会产生方法调用栈，最终也取决于两个链表的长度
     *
     * @param listNode1
     * @param listNode2
     * @return
     */
    public static ListNode merge(ListNode listNode1, ListNode listNode2) {

        if (listNode1 == null) {
            return listNode2;
        } else if (listNode2 == null) {
            return listNode1;
        } else if (listNode1.val < listNode2.val) {
            // listNode1比listNode2小，所以此时将listNode2和listNode1.next的比较结果
            // 放在listNode1.next属性下
            listNode1.next = merge(listNode1.next, listNode2);
            return listNode1;
        } else {
            listNode2.next = merge(listNode2.next, listNode1);
            return listNode2;
        }
    }

    /**
     * 迭代解决:
     *   启动一个哑结点(用来连接迭代后的节点)
     *   启动一个preNode, 作为迭代器, 后续将使用它来
     *
     * @param listNode1
     * @param listNode2
     * @return
     */
    public static ListNode iterate(ListNode listNode1, ListNode listNode2) {
        // 用来存放排序后的链表
        ListNode preHead = new ListNode(-1);

        // 用来做比较和迭代, 指向上述new出来的preHead，用preNode来操作preHead
        ListNode preNode = preHead;

        while (listNode1 != null && listNode2 != null) {
            // 将小的一个节点移动至preNode的下一个节点
            if (listNode1.val > listNode2.val) {
                preNode.next = listNode2;

                // 后续将开始处理listNode2的next节点
                listNode2 = listNode2.next;

            } else {
                // listNode1.val <= listNode2.val的情况
                preNode.next = listNode1;
                listNode1 = listNode1.next;
            }

            // 获取preNode的下一个节点(当前比较后比较小的那个节点)，后续将对比较小的一个节点放在
            // preNode的下一个节点的next属性上
            // 这里修改了preNode的引用，我之前还困扰着说，preNode的引用都不指向PreHead了
            // 怎么还能修改preHead的值呢？
            // 后来还是自己基础不够，因为preHead是一个链表，preNode.next标识链表next属性指向的
            // 节点。preNode的确是不指向preHead了，但是它指向的是preHead的next属性的对象
            // 以此类推
            preNode = preNode.next;
        }

        // 上述while循环遍历完后，至少有一个节点没有被处理,
        // 假设遍历到listNode1的最后一个节点了，处理完后，发现它的next为null
        // 进而退出了循环，此时listNode2的最后一个节点还未处理，此时把它假如到preNode下即可
        preNode.next = listNode1 == null ? listNode2 : listNode1;

        // 获取preHead的下一个，因为第一个为-1，用来连接节点时使用的
        return preHead.next;
    }

}
