package leetcode;

import java.util.ArrayDeque;
import java.util.Deque;
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
 * 输入: nums = [1, 3, -1, -3, 5, 3, 6, 7], 和 k = 3
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
 * 分析:
 *
 *   解法1： 使用大根堆
 *   1. 一个降序队列
 *   2. 每次进入队列时，进入后若队列元素刚好满3个，则拿出第一个作为最大值
 *   3. 当队列长度为3时，新进入的元素要跟第一个值(最大值)相比，若比它小，则直接
 *      取出第一个元素继续作为最大值，并且重新维护队列，将上一次进入的元素挪出去
 *
 *   解法2： 使用双向队列
 *
 *   滑动窗口中，有一端进有一端出。
 *   所以需要一个双向队列
 *   在java中有个数据结构叫Deque
 *
 *
 */
public class MaxSlidingWindow {

    /**
     * 解法1：
     * 使用有序队列实现(大根堆)
     * 使用大根堆作为滑动窗口，每次滑动窗口移动时，最大值在第一个
     *
     * 在leetcode中提交时，提示执行时间超时，可见时间复杂度不好，
     *
     * @param nums
     * @param k
     * @return
     */
    public static int[] maxSlidingWindow1(int[] nums, int k) {

        // 存储滑动窗口最大值的数组
        int[] result = new int[nums.length - k + 1];

        // 降序队列 ---> 大根堆
        PriorityQueue<Integer> queue = new PriorityQueue<>((x, y) -> {
            return y - x;
        });

        int currentWindowMaxVal = 0;
        int resultIndex = 0;

        for (int i = 0; i < nums.length; i++) {
            int num = nums[i];

            if (queue.size() < k) {
                queue.offer(num);
            }

            if (queue.size() == k) {
                // 第一个元素是最大的
                currentWindowMaxVal = queue.peek() >= num ? queue.peek() : num;
                result[resultIndex++] = currentWindowMaxVal;
                // 移除上一个元素
                queue.remove(nums[i - k + 1]);
            }
        }

        return result;
    }

    /**
     * 解法2：
     *   使用双向队列,
     *   若后面进来滑动窗口的元素，比滑动窗口中的所有元素都大，
     *   则可以删除滑动窗口中的所有元素，然后把这个新元素放入滑动窗口中。
     *   这样的话，滑动窗口中的第一个元素永远是最大的
     * @param nums
     * @param k
     * @return
     */
    public static int[] maxSlidingWindow2(int[] nums, int k) {

        if (nums == null || k == 1) {
            return nums;
        }

        // 存储滑动窗口最大值的数组
        int[] result = new int[nums.length - k + 1];
        Deque<Integer> deque = new ArrayDeque<>();

        int resultIndex = 0;

        for (int i = 0; i < nums.length; i++) {

            int num = nums[i];

            while ((deque.peekFirst() != null && deque.peekFirst() < num ) || deque.size() >= k) {
                deque.pollFirst();
            }

            deque.offer(num);

            if (i >= k - 1) {
                // 求队列中的最大值
                int maxVal = 0;
                for (Integer integer : deque) {
                    maxVal = integer > maxVal ? integer : maxVal;
                }
                result[resultIndex++] = maxVal;
            }
        }

        return result;
    }


    public static void main(String[] args) {
        int[] nums = new int[] {1, 3, 1, 2, 0, 5};
        int k = 3;
        System.out.println(maxSlidingWindow2(nums, k));
    }
}
