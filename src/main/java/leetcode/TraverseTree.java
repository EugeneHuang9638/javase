package leetcode;

import leetcode.model.TreeNode;

import java.util.ArrayList;
import java.util.List;

/**
 * 遍历一棵树
 *
 * 先序遍历：根 -> 左 -> 右
 * 中序遍历：左 -> 中 -> 右
 * 后序遍历：左 -> 右 -> 中
 *
 */
public class TraverseTree {


    /**
     * 根 -> 左 -> 右
     * @param root
     * @return
     */
    public static List<Integer> preorder(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        if (root == null) {
            return list;
        }

        // 存储根节点
        list.add(root.val);

        if (root.leftNode != null) {
            list.addAll(preorder(root.leftNode));
        }

        if (root.rightNode != null) {
            list.addAll(preorder(root.rightNode));
        }

        return list;
    }

    public static List<Integer> inorder(TreeNode root) {
        List<Integer> list = new ArrayList<>();
        if (root == null) {
            return list;
        }

        if (root.leftNode != null) {
            list.addAll(inorder(root.leftNode));
        }

        list.add(root.val);

        if (root.rightNode != null) {
            list.addAll(inorder(root.rightNode));
        }

        return list;
    }

    public static List<Integer> postorder(TreeNode root) {
        List<Integer> list = new ArrayList<>();

        if (root == null) {
            return list;
        }

        // 存储左节点
        if (root.leftNode != null) {
            list.addAll(postorder(root.leftNode));
        }

        // 处理右节点
        if (root.rightNode != null) {
            list.addAll(postorder(root.rightNode));
        }

        // 处理根节点
        list.add(root.val);

        return list;
    }


    public static void main(String[] args) {
        TreeNode t1 = new TreeNode(1, null, null);
        TreeNode t4 = new TreeNode(4, null, null);
        TreeNode t3 = new TreeNode(3, t1, t4);

        TreeNode t9 = new TreeNode(9, null, null);
        TreeNode t7 = new TreeNode(7, null, t9);

        TreeNode t5 = new TreeNode(5, t3, t7);
        System.out.println(preorder(t5));
        System.out.println(inorder(t5));
        System.out.println(postorder(t5));

    }


}
