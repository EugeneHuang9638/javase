package leetcode;

import leetcode.model.TreeNode;

import java.util.LinkedList;

/**
 * 给定一个二叉树, 找到该树中两个指定节点的最近公共祖先。
 *
 * 百度百科中最近公共祖先的定义为：“对于有根树 T 的两个结点 p、q，
 * 最近公共祖先表示为一个结点 x，满足 x 是 p、q 的祖先且 x 的深度尽可能大（一个节点也可以是它自己的祖先）。”
 *
 * 例如，给定如下二叉树:  root = [3,5,1,6,2,0,8,null,null,7,4]
 *
 * 示例 1:
 * 输入: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
 * 输出: 3
 * 解释: 节点 5 和节点 1 的最近公共祖先是节点 3。
 *
 * 示例 2:
 * 输入: root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4
 * 输出: 5
 * 解释: 节点 5 和节点 4 的最近公共祖先是节点 5。因为根据定义最近公共祖先节点可以为节点本身。
 *
 *
 */
public class BTLowestCommonAncestor {

    /**
     *
     * 思路：在根节点中去找p或者q，找到哪个返回哪个
     *
     * 如果根节点为空或者根节点为p或者根节点为q，那么根节点肯定就是最小公共祖先
     * 这就是最基本的出口
     *
     * 若不满足出口，则同时从根的左右两边去找p或者q，
     * 如果从左节点去查找p或者q，
     *   把左节点作为根节点，基于它的左右节点继续找
     *   如果左节点为null，那就肯定在右节点
     *   如果右节点为null，那就肯定在左节点
     *   如果左右节点同时为null，那就直接返回根节点
     *
     * 所以判断p、q节点最小公共祖先的问题就演变成了：
     * 从根节点开始去查询p、q两个节点的问题，
     * 同时从左右节点开始查
     * 如果
     *   左节点找不到，那就肯定在右节点，
     *   右节点找不到，那就肯定在左节点
     *   左右节点都找不到，则返回根节点
     * @param root
     * @param p
     * @param q
     * @return
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        return findPorQ(root, p, q);
    }

    public TreeNode findPorQ(TreeNode root, TreeNode p, TreeNode q) {
        // 同理，只要root为p或者为q时，最小公共祖先肯定是root
        if (root == null || root == p || root == q) {
            return root;
        }

        // 从根的左边去找p或者q
        TreeNode left = lowestCommonAncestor(root.left, p, q);

        // 从根的右边去找p或者q
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        // 左边找不到，那就肯定在右边，于是返回right
        if (left == null) {
            return right;
        }

        // 右边找不到，那就肯定在左边，于是返回left
        if (right == null) {
            return left;
        }

        // 两边都找不到，那就返回root
        return root;
    }

}
