package leetcode;

import leetcode.model.TreeNode;

import java.util.*;

/**
 * 深度优先搜索算法
 *
 * 所谓深度优先算法类似于计算机的思想，一条路走到底
 *
 * 给定一颗二叉树：[1, 2, 3, 4, 5, 6, 7]
 * =>
 *      1
 *   2    3
 * 4  5 6  7
 *
 * 使用深度优先搜索算法遍历之后的结果为
 * 1, 2, 4, 5, 3, 6, 7
 */
public class DeepFirstSearch {

    /**
     * 使用分治法 遍历的方式解决
     * 从左节点开始遍历
     * @param root
     * @return
     */
    public static List<Integer> deepFirstSearch(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        result.add(root.val);
        if (root.left != null) result.addAll(deepFirstSearch(root.left));
        if (root.right != null) result.addAll(deepFirstSearch(root.right));
        return result;
    }


    static Stack<TreeNode> stack = new Stack<>();
    static Set<TreeNode> visited = new HashSet<>();

    /**
     * 使用迭代的方式来完成
     * @param root
     * @return
     */
    public static List<Integer> deepFirstSearch2(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode tmp = stack.pop();
            if (tmp == null || visited.contains(tmp)) {
                continue;
            }
            visited.add(tmp);
            result.add(tmp.val);
            stack.push(tmp.right);
            stack.push(tmp.left);
        }

        return result;
    }

    public static void main(String[] args) {
        TreeNode t4 = new TreeNode(4, null, null);
        TreeNode t5 = new TreeNode(5, null, null);
        TreeNode t2 = new TreeNode(2, t4, t5);

        TreeNode t6 = new TreeNode(6, null, null);
        TreeNode t7 = new TreeNode(7, null, null);
        TreeNode t3 = new TreeNode(3, t6, t7);

        TreeNode t1 = new TreeNode(1, t2, t3);
        System.out.println(deepFirstSearch(t1));
//        System.out.println(deepFirstSearch2(t1));
    }
}
