package leetcode;


import leetcode.model.TreeNode;

import java.util.*;

/**
 * 实现广度优先搜索算法(地毯式搜索)
 * 所谓广度优先算法就是遍历当前树的同级节点，
 * 然后把他们放在队列中去，
 * 同时也把他们放入hash表中，来预防重复的遍历.
 *
 * 当前同级节点遍历完之后，再遍历下一级的同级节点
 *
 * 题目：
 *
 * 给定一颗二叉树，使用特定的广度优先搜索算法遍历
 * eg: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
 *
 *            1
 *       2          3
 *    4    5     6     7
 *  8  9 10 11 12 13 14 15
 * 深度为奇数时，从右往左遍历，
 * 深度为偶数时，从左往右遍历
 *
 * 遍历后：1237654
 *
 */
public class BreadthFirstSearch {

    // 存储当前要处理的节点
    static LinkedList<TreeNode> queue = new LinkedList<>();

    static Set<TreeNode> visited = new HashSet<>();

    /**
     * 蛇形遍历树：
     *
     * 首先遍历树一般有前、中、后序三种方式，除此之外还有广度优先、深度优先遍历
     *
     * 在此题中，使用的是广度优先遍历方式。
     *
     * 其中有这么一个特点： 若当前层是处于奇数层，那么当前层的遍历方式是从右往左，且下一层的遍历方式为从左往右
     *
     * 在广度优先搜索算法中，一般会使用到一个queue和一个visited的去重set。
     * 因为此题给的二叉树，所以不会出现节点重复的情况，所以visited的set集合可以去掉。
     * 同时因为有特殊顺序的原因，所以解决此题的关键点为：一个flag标识 + queue + stack
     * 其中flag标识用来标识当前遍历的层是奇数层还是偶数层，
     * queue为广度优先搜索算法的基本队列
     * stack为确定当前节点的子节点是如何遍历的，是左节点往右节点遍历还是从右节点从左节点遍历。
     *
     *
     *
     * @param root
     * @return
     */
    public static List<Integer> specBFS(TreeNode root) {
        List<Integer> list = new ArrayList<>();

        queue.offer(root);

        Stack<TreeNode> stack = new Stack<>();

        // 层数，若对2取模 == 1, 则证明是奇数层，此时孩子从左往右开始进入队列
        int level = 1;

        while (!queue.isEmpty()) {
            // 获取到当前层拥有的节点个数 currentLevelSize，
            int currentLevelSize = queue.size();
            // flag == true  ==> 当前层为奇数层, 栈中的左节点先入队列
            boolean flag = level++ % 2 == 1;

            // 遍历currentLevelSize次，目的是把当前层的所有元素都遍历到
            for (int i = 0; i < currentLevelSize; i++) {
                // 从队列头取出一个元素，并把它加入到list中，表示它遍历过了，
                // 同时要把它压栈
                TreeNode tmp = queue.poll();
                if (tmp == null) continue;

                list.add(tmp.val);
                stack.push(tmp);
            }

            // 遍历栈，此时栈中存的是当前元素遍历完的个数，并且是按照一定的顺序存入的，
            // 若当前层是奇数层，则下一层是从左往右遍历，即左节点先进队列。
            while (!stack.isEmpty()) {
                TreeNode tmp = stack.pop();
                queue.offer(flag ? tmp.left : tmp.right);
                queue.offer(flag ? tmp.right : tmp.left);
            }
        }

        return list;
    }

    public static List<Integer> bfs(TreeNode root) {
        List<Integer> list = new ArrayList<>();

        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode tmp = queue.poll();
            if (visited.contains(tmp)) continue;
            visited.add(tmp);
            // 队列中的当前节点没有被遍历过，则加入到list中去
            list.add(tmp.val);

            // 从左往右，遍历队列中的元素，每个节点的右节点先进队列, 左节点次之
            if (tmp.left != null) queue.offer(tmp.left);
            if (tmp.right != null ) queue.offer(tmp.right);
        }

        return list;
    }

    public static void main(String[] args) {

        TreeNode t8 = new TreeNode(8, null, null);
        TreeNode t9 = new TreeNode(9, null, null);
        TreeNode t4 = new TreeNode(4, t8, t9);

        TreeNode t10 = new TreeNode(10, null, null);
        TreeNode t11 = new TreeNode(11, null, null);
        TreeNode t5 = new TreeNode(5, t10, t11);
        TreeNode t2 = new TreeNode(2, t4, t5);

        TreeNode t12 = new TreeNode(12, null, null);
        TreeNode t13 = new TreeNode(13, null, null);
        TreeNode t6 = new TreeNode(6, t12, t13);

        TreeNode t14 = new TreeNode(14, null, null);
        TreeNode t15 = new TreeNode(15, null, null);
        TreeNode t7 = new TreeNode(7, t14, t15);
        TreeNode t3 = new TreeNode(3, t6, t7);

        TreeNode t1 = new TreeNode(1, t2, t3);

        // System.out.println(bfs(t1));
        System.out.println(specBFS(t1));

    }
}
