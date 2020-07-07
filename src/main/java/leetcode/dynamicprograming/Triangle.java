package leetcode.dynamicprograming;


/**
 * 给定一个三角形，求从上往下，最短路径和
 *
 * [
 *      [2],
 *     [3,4],
 *    [6,5,7],
 *   [4,1,8,3]
 * ]
 *
 * 结果为： 2 + 3 + 5 + 1 = 11
 *
 *
 * 注意：虽然是个三角形，但其实它就是一个二维数组，还原后的结构如下：
 * [
 *   [2],
 *   [3, 4],
 *   [6, 5, 7],
 *   [4, 1, 8, 3]
 * ]
 *
 *
 * 我们使用递归分析：
 * f(2)走向底部最短路径的值为: 2 + min(f(3), f(4))
 * f(3)走向底部最短路径的值为: 3 + min(f(6), f(5))
 * f(4)走向底部最短路径的值为: 4 + min(f(5), f(7))
 * ......
 *
 * 由上可知，有重复的计算，比如f(5)重复计算了两次
 * 按照动态规划的结题思路:
 * 1. 递归 + 记忆化
 * 2. 状态的定义
 * 3. 状态转移方法
 *
 * 咱们已经做了递归分析，记忆化就算了，毕竟咱们不使用递归来解决此问题
 *
 * 于是我们来进行状态的定义步骤：
 * 1. 我们定义dp[m][n]表示的含义为: 从[m, n]这个点走到最底层的最短路径和
 *
 * 所以状态转移方程可以表示为：
 * dp[m][n] = num[m][n] + min(dp[m+1][n], dp[m+1][n+1])
 *
 * 其中我们能知道的值为: 最底部的那些行的最小路径和就是dp[m][n]
 *
 * 其中，最小值我们需要额外定义个变量来保存它。
 */
public class Triangle {

    public static int solution(int[][] triangle) {
        int dp[][] = new int[triangle.length][triangle[triangle.length - 1].length];

        for (int i = triangle.length - 1; i >= 0; i--) {
            for (int j = 0; j < triangle[i].length; j++) {
                // 附上初始值
                if (i == triangle.length - 1) {
                    dp[i][j] = triangle[i][j];
                } else {
                    dp[i][j] = triangle[i][j] + Math.min(dp[i + 1][j], dp[i + 1][j + 1]);
                }

            }
        }

        return dp[0][0];
    }


    public static void main(String[] args) {
        int[][] triangle = new int[][] {
                {2},
                {3, 4},
                {6, 5, 7},
                {4, 1, 8, 3}
        };

        System.out.println(solution(triangle));
    }
}
