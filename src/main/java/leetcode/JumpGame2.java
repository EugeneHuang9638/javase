package leetcode;

/**
 *
 * 恶补数据结构去！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
 *
 *
 * 跳跃游戏 II
 *
 * 给定一个非负整数数组，你最初位于数组的第一个位置。
 *
 * 数组中的每个元素代表你在该位置可以跳跃的最大长度。
 *
 * 你的目标是使用最少的跳跃次数到达数组的最后一个位置。
 *
 * 示例:
 *
 * 输入: [2,3,1,1,4]   ====>
 * 输出: 2
 * 解释: 跳到最后一个位置的最小跳跃数是 2。
 *      从下标为 0 跳到下标为 1 的位置，跳 1 步，然后跳 3 步到达数组的最后一个位置。
 * 说明:
 *
 * 假设你总是可以到达数组的最后一个位置。
 *
 * ==========================================
 * 分析:
 *   题目要求的是从数组的第一个元素要跳跃几次能达到数组的最后一个位置
 *   其中数组中的每个value，代表在此位置下可以跳跃的数量
 *
 *   又是一个最值问题： 此时的最值问题为"最小跳跃次数"
 *
 * 那么我们进行如下操作:
 * 1. 定义子问题
 * dp[i - 1]为nums的第i-1个位置上可以跳跃到数组最后一个位置的最小跳跃数
 *
 * i = 0, nums[i] = 2, 设dp[i - 1] = dp[0] = Integer.MAX_VALUE;
 * i = 1, nums[i] = 3, dp[i - 1] = i + nums[i] = 1 + 3 >= numx.length - i = 4
 *   ===>  dp[i-1] =
 *
 * 2. 定义递推关系
 * dp[i] = dp[i - 1]
 *
 * if (dp的index + nums[dp的index] >= numx.length - i) {
 *     此时的最小值为dp[i - 1]
 * }
 *
 * 3. 设置初始值
 * dp[0]表示数组中第一个位置跳跃到最后一个元素的最小跳跃数
 * dp[0] = 假设为0为Integer的max
 *
 *
 *
 * index + step >= 4  那就是返回step + index
 *
 */
public class JumpGame2 {

    public static int jump(int nums[]) {
        // 边界，假设数组只有一个长度，那么则跳跃0次
        int step = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] + step >= nums.length - 1) {
                return step;
            } else {
                step += nums[i];
            }
        }
        return step;
    }


    public static void main(String[] args) {
        int[] nums = new int[] {2, 3, 1, 1, 4};
        System.out.println(jump(nums));
    }
}
