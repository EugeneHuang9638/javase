package leetcode;

/**
 * 最大子序和
 *
 * 给定一个整数数组 nums ，找到一个具有最大和的连续子数组（子数组最少包含一个元素），返回其最大和。
 *
 * 输入: [-2, 1, -3, 4, -1, 2, 1, -5, 4],
 * 输出: 6
 * 解释: 连续子数组 [4, -1, 2, 1] 的和最大，为 6。
 *
 * 动态规划一般处理的就是'最'值问题。
 *
 *
 * 步骤:
 *  1. 抽象出子问题(假设存在一个最大和的连续子数组并在存到dp数组中)
 *    假设dp[i-1]为nums中第i-1元素的最大和的连续子数组(从0到i-1连续计算的)
 *
 *  2. 列出递推关系(已知i-1如何求i)
 *    上述已经列出了numx中第i-1个元素的最大和的连续子数组，现在求dp[i]
 *    因为dp[i-1]为nums从0到i-1的最大子数组和
 *    现在要求nums第i个元素的最大子数组和 ----> 此时变得很简单，只需要把nums[i]与dp[i-1]相加即可
 *
 *    所以有 ********dp[i] = max(nums[i], dp[i-1] + nums[i])******** 的表达式
 *    这里为什么要与nums[i]作比较呢？因为有可能dp[i-1] + nums[i]比nums[i]还要小
 *    此时我们一个nums[i]都可以作为一个最大和的连续子数组了(尽管只有一个元素)
 *
 *  3. 设置dp的起始值，因为dp数组表示的nums当前数组下标的最大连续子数组和
 *     dp的起始值，我们设置成nums数组起始元素的连续子数组的最大和即: -2
 *
 *
 *  递归分析：
 *    f(-2) = -2
 *    f(1) = f(-2) + f(1)
 *    f(-3) = f(1) + -3
 *
 *    重复计算了f(1)
 *
 *  我们定义： dp[n] 从0 -> n 的数组中最大连续数组的和
 *  那么 dp[n] = max(num[n], dp[n -1] + num[n])
 *
 *  初始值：dp[o] = -2;
 *
 *  然后再从dp数组中找出最大的值。或者使用一个变量，存储最大值
 *
 *
 */
public class MaxSubArray {

    /**
     * dp数组: 存放的是nums中每个下标对应的最大和连续子数组的值
     * @param nums
     * @return
     */
    public static int sum(int nums[]) {
        int result = nums[0];
        int dp[] = new int[nums.length];

        dp[0] = nums[0];

        for (int i = 1; i < nums.length; i++) {
            dp[i] = Math.max(nums[i], dp[i - 1] + nums[i]);
            result = Math.max(result, dp[i]);
        }

        return result;
    }

    public static void main(String[] args) {
        int nums[] = new int[] {-2, 1, -3, 4, -1, 2, 1, -5, 4};

        System.out.println(sum(nums));
    }
}
