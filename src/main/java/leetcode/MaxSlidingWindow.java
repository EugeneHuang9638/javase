package leetcode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 给定一个数组 nums，
 * 有一个大小为 k 的滑动窗口从数组的最左侧移动到数组的最右侧。
 * 你只可以看到在滑动窗口内的 k 个数字。
 * 滑动窗口每次只向右移动一位。
 *
 * 返回滑动窗口中的最大值。
 *
 * 示例:
 *
 * 输入: nums = [1,3,-1,-3,5,3,6,7], 和 k = 3
 * 输出: [3,3,5,5,6,7]
 * 解释:
 *
 *   滑动窗口的位置                最大值
 * ---------------               -----
 * [1  3  -1] -3  5  3  6  7       3
 *  1 [3  -1  -3] 5  3  6  7       3
 *  1  3 [-1  -3  5] 3  6  7       5
 *  1  3  -1 [-3  5  3] 6  7       5
 *  1  3  -1  -3 [5  3  6] 7       6
 *  1  3  -1  -3  5 [3  6  7]      7
 *
 */
public class MaxSlidingWindow {

    /**
     * 使用有序队列实现(大根堆)
     * 使用大根堆作为滑动窗口，每次滑动窗口移动时，最大值在第一个
     *
     * @param nums
     * @param k
     * @return
     */
    public static int[] maxSlidingWindow(int[] nums, int k) {
        // 边界情况
        /*if (nums.length < k || k == 1) {
            return nums;
        }*/

        // 存储滑动窗口最大值的数组
        int[] result = new int[nums.length - k + 1];

        // 降序队列 ---> 大根堆
        PriorityQueue<Integer> queue = new PriorityQueue<>((x, y) -> {
            return y - x;
        });

        int currentWindowMaxVal = 0;
        int resultIndex = 0;

        for (int num : nums) {

            if (queue.size() < k) {
                queue.offer(num);
            }

            if (queue.size() == k) {
                // 第一个元素是最大的
                if (queue.peek() >= num) {
                    currentWindowMaxVal = queue.peek();
                } else {
                    currentWindowMaxVal = num;
                    // 移除上一个元素
                    queue.remove();
                    queue.offer(num);
                }

                result[resultIndex++] = currentWindowMaxVal;
            }

        }

        return result;
    }

    public static void main(String[] args) {
        int[] nums = new int[] {1, 3, -1, -3, 5, 3, 6, 7};
        int k = 3;
        System.out.println(maxSlidingWindow(nums, k));
    }
}
