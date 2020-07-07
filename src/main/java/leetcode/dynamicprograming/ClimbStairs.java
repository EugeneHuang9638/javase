package leetcode.dynamicprograming;

/**
 * leetcode70题 爬楼梯
 *
 * 给定一个n阶楼梯，小人从下往上爬，每次只能爬一阶或二阶楼梯
 * 问： 有多少走法？
 *
 *
 * eg:
 *   给定一个3阶楼梯，小人一共有3种走法
 *         _    第三层 爬到第三层的走法为 爬到第一层的走法 + 爬到第二层的走法
 *       _|    第二层 爬到第二层的走法为两种  一步一步的走，或者直接走两步  => 1, 1 和  2
 *     _|     第一层  爬到第一层的走法为一种
 *  人 |
 *
 *
 */
public class ClimbStairs {


    public static int solution(int n) {
        if (n <= 1) {
            return 1;
        }

        // 定义状态 dp[n]表示走到第n阶的走法
        int dp[] = new int[n];

        // 所以走到第一阶的走法为走一步
        dp[0] = 1;

        // 走到第二阶的走法为：一步一步的走或者一次性走两步
        dp[1] = 2;

        for (int i = 2; i < n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n - 1];
    }

    public static void main(String[] args) {
        System.out.println(solution(3));
    }

}
