package leetcode;

import leetcode.model.TreeNode;

/**
 * 给定一个二叉搜索树, 找到该树中两个指定节点的最近公共祖先。
 *
 * 百度百科中最近公共祖先的定义为：
 * “对于有根树 T 的两个结点 p、q，最近公共祖先表示为一个结点 x，
 * 满足 x 是 p、q 的祖先且 x 的深度尽可能大（一个节点也可以是它自己的祖先）。”
 *
 * 例如，给定如下二叉搜索树:  root = [6,2,8,0,4,7,9,null,null,3,5]
 *
 * 示例一:
 * 输入: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
 * 输出: 6
 * 解释: 节点 2 和节点 8 的最近公共祖先是 6。
 *
 * 示例二：
 * 输入: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 4
 * 输出: 2
 * 解释: 节点 2 和节点 4 的最近公共祖先是 2, 因为根据定义最近公共祖先节点可以为节点本身。
 *
 *
 * 说明：
 * 所有节点的值都是唯一的。
 * p、q 为不同节点且均存在于给定的二叉搜索树中。
 */
public class BSTLowestCommonAncestor {


    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // 如果p或者q就是root节点，那么说明，root就是他们的最小公共组先

        return findPorQ(root, p, q);
    }


    /**
     * 给定一个根节点，从根节点中去找p或者q
     *
     * 1. 如果p和q不同时位于左子树或右子树，那么root肯定为最小公共组先
     *    因为root是一颗二叉查找树，那么只要证明，p在root左边，q在root右边，或者p在root右边，q在root左边
     *    即可证明p和q不同时位于左子树或右子树
     * 2. 如果p和q有一个就是root，那么root本身肯定为最小公共组先
     * 3. 如果p和q同时在左子树，那么再基于root.left做一次findPorQ的逻辑
     * 4. 如果p和q同时在右子树，那么再基于root.right做一次findPorQ的逻辑
     *
     * @param root
     * @param p
     * @param q
     * @return
     */
    public TreeNode findPorQ(TreeNode root, TreeNode p, TreeNode q) {
        if (root == p || root == q || ((root.val > p.val && root.val < q.val) || root.val < p.val && root.val > q.val)) {
            return root;
        } else if (root.val > p.val) {
            //从左子树中去找p, 说明p是在左子树中
            return findPorQ(root.left, p, q);
        } else {
            // 说明，从右子树中去找q
            return findPorQ(root.right, p, q);
        }
    }

}
