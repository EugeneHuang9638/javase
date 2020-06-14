package leetcode;


import leetcode.model.TreeNode;

import java.util.*;

/**
 *
 * 给你一个二叉树，请你返回其按 层序遍历 得到的节点值。 （即逐层地，从左到右访问所有节点）。
 *
 *
 * 给定二叉树
 * 二叉树：[3,9,20,null,null,15,7],
 *       3
 *    9    20
 *       15  7
 *

 * 返回层次结果:
 *  [
 *   [3],
 *   [9,20],
 *   [15,7]
 * ]
 *
 */
public class BinaryTreeLevelOrder {


    /**
     * 使用BFS(广度优先查找算法)思想解决此问题
     *
     * 在处理当前节点时，
     * 用一个list来存储当前节点的元素，
     * eg: 在遍历root元素时，首先创建一个list
     * 获取当前队列中的长度 n，
     * 随后遍历n次，每次从队列中取出一个元素放如list中去，并把它的左右(左右非空)元素放入队列中去(方便下一次遍历)
     *
     * 所以这么一次处理后，每次while循环时，队列中的元素都是当前层的所有元素
     * @param root
     * @return
     */
    public static List<List<Integer>> levelOrderBFS(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();

        if (root == null) return result;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        // 在二叉树中不会存在重复的遍历节点，所以不需要这个visited去重的set
//        Set<TreeNode> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            // 当前层级下的节点个数
            int currentLevelSize = queue.size();
            // 存储当前层级的所有节点
            List<Integer> currentLevelNodes = new ArrayList<>();
            for (int i = 0; i < currentLevelSize; i++) {
                TreeNode tmp = queue.poll();
                currentLevelNodes.add(tmp.val);

                if (tmp.left != null) queue.offer(tmp.left);
                if (tmp.right != null) queue.offer(tmp.right);
            }

            result.add(currentLevelNodes);
        }
        return result;
    }

    /**
     * 使用dfs方式完成二叉树的层序遍历
     * @param root
     * @return
     */
    public static List<List<Integer>> levelOrderDFS(TreeNode root) {
        List<List<Integer>> container = new ArrayList<>();

        return dfs(1, container, root);
    }

    public static List<List<Integer>> dfs(int level, List<List<Integer>> container, TreeNode root) {
        if (root == null) {
            return container;
        }

        if (container.get(level - 1) == null) {
            container.add(new ArrayList<>());
        }


        return container;
    }


    public static void main(String[] args) {

        TreeNode t15 = new TreeNode(15, null, null);
        TreeNode t7 = new TreeNode(7, null, null);

        TreeNode t20 = new TreeNode(20, t15, t7);
        TreeNode t9 = new TreeNode(9, null, null);

        TreeNode t3 = new TreeNode(3, t9, t20);

        System.out.println(levelOrderBFS(t3));

    }
}
