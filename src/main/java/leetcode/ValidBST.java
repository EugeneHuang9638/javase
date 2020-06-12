package leetcode;

import leetcode.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 给定一个二叉树，判断其是否是一个有效的二叉搜索树。
 *
 * 假设一个二叉搜索树具有如下特征：
 *
 * 节点的左子树只包含小于当前节点的数。
 * 节点的右子树只包含大于当前节点的数。
 * 所有左子树和右子树自身必须也是二叉搜索树。
 *
 *
 * 二叉查找树定义:
 *  一棵空树或者具有如下特征的树：
 *    1. 节点的左子树中的所有节点都小于当前节点
 *    2. 节点的右子树中的所有节点都大于当前节点
 *    3. 所有左子树和右子树自身必须也是二叉查找树
 *
 * 分析：
 *   按照二叉查找树的特性，左子树都小于根节点，右子树都大于根节点
 *
 *
 *   那么我们可以对树做一个中序遍历，如果遍历后的结果是一个升序数组，
 *   则表示为二叉查找树。在此过程中，我们可以记录每次遍历的最大值
 *
 *   时间复杂度为O(n), 每个节点都要访问一次
 *
 *
 *
 */
public class ValidBST {

    /**
     * 中序遍历
     * @return
     */
    public static List<Integer> inorder(TreeNode root) {
        List<Integer> list = new ArrayList<>();

        if (root == null) {
            return list;
        }

        if (root.left != null) {
            list.addAll(inorder(root.left));
        }

        list.add(root.val);

        if (root.right != null) {
            list.addAll(inorder(root.right));
        }

        return list;
    }


    public static boolean isValidBST(TreeNode root) {
        if (root == null) {
            return true;
        }

        // 对树进行中序遍历，如果是一颗二叉查找树，那么它的中序遍历后就是一个升序的数组
        List<Integer> sortedArr = inorder(root);
        System.out.println(sortedArr);

        for (int i = 0; i < sortedArr.size(); i++) {
            if (i + 1 >= sortedArr.size()) {
                break;
            }

            if (sortedArr.get(i) >= sortedArr.get(i + 1)) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        TreeNode t1 = new TreeNode(1, null, null);
        TreeNode t3 = new TreeNode(3, null, null);
        TreeNode t2 = new TreeNode(2, t1, t3);

        System.out.println(isValidBST(t2));

        TreeNode t5 = new TreeNode(5, null, null);

        TreeNode t6 = new TreeNode(6, null, null);
        TreeNode t20 = new TreeNode(20, null, null);

        TreeNode t15 = new TreeNode(15, t6, t20);


        TreeNode t10 = new TreeNode(10, t5, t15);
        System.out.println(isValidBST(t10));
    }
}
