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
 * eg: [1, 2, 3, 4, 5, 6, 7]
 *
 *     1
 *   2   3
 *  4 5 6 7
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

    // 从左往右
    static boolean priorityFlag = true;

    public static List<Integer> specBFS(TreeNode root) {
        List<Integer> list = new ArrayList<>();

        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode tmp = queue.poll();
            if (visited.contains(tmp)) continue;
            visited.add(tmp);
            // 队列中的当前节点没有被遍历过，则加入到list中去
            list.add(tmp.val);

            LinkedList<TreeNode> linkedListClone = (LinkedList<TreeNode>) queue.clone();
            if (priorityFlag) {
                // 倒序遍历, 将右节点先进
                TreeNode lastNode = linkedListClone.pollLast();

                if (lastNode == null) {
                    if (tmp.right != null ) queue.offer(tmp.right);
                    if (tmp.left != null) queue.offer(tmp.left);
                }

                while (lastNode != null) {
                    if (lastNode.left != null) queue.offer(lastNode.left);
                    if (lastNode.right != null ) queue.offer(lastNode.right);

                    lastNode = linkedListClone.pollLast();
                }


            } else {
                // 正序遍历，将左节点先进
                TreeNode firstNode = linkedListClone.poll();

                if (firstNode == null) {
                    if (tmp.left != null) queue.offer(tmp.left);
                    if (tmp.right != null ) queue.offer(tmp.right);
                }

                while (firstNode != null) {
                    if (firstNode.left != null) queue.offer(firstNode.left);
                    if (firstNode.right != null ) queue.offer(firstNode.right);

                    firstNode = linkedListClone.poll();
                }

            }

            priorityFlag = !priorityFlag;
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
        TreeNode t4 = new TreeNode(4, null, null);
        TreeNode t5 = new TreeNode(5, null, null);
        TreeNode t2 = new TreeNode(2, t4, t5);


        TreeNode t6 = new TreeNode(6, null, null);
        TreeNode t7 = new TreeNode(7, null, null);
        TreeNode t3 = new TreeNode(3, t6, t7);

        TreeNode t1 = new TreeNode(1, t2, t3);

        // System.out.println(bfs(t1));
        System.out.println(specBFS(t1));

    }
}
