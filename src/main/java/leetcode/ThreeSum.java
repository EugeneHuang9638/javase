package leetcode;


import java.util.*;

/**
 * 给你一个包含 n 个整数的数组 nums，判断 nums 中是否存在三个元素 a，b，c ，
 * 使得 a + b + c = 0 ？请你找出所有满足条件且不重复的三元组。
 *
 * 注意：答案中不可以包含重复的三元组。
 *
 * 示例：
 *
 * 给定数组 nums = [-1, 0, 1, 2, -1, -4]，
 *
 * 满足要求的三元组集合为：
 * [
 *   [-1, 0, 1],
 *   [-1, -1, 2]
 * ]
 *
 * 分析：
 *   解法一：暴力破解法
 *     三层循环
 *
 *   解法二：
 *     和求两数之和类似，这里可以用两层循环，第三层循环从hash表中去找
 *
 *
 * map和set经常用来的做法是查询和计数
 *
 */
public class ThreeSum {

    public static List<List<Integer>> threeSum(int[] nums) {
        if (nums.length < 3) {
            List<List<Integer>> lists = new ArrayList<>();
            List<Integer> inner = new ArrayList<>();

            for (int num : nums) {
                inner.add(num);
            }

            return lists;
        }

        List<List<Integer>> result = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();

        Set<Integer> repeat = new HashSet<>();

        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], i);
        }

        for (int i = 0; i < nums.length; i++) {
            int a = nums[i];
            for (int j = i + 1; j < nums.length; j++) {
                int b = nums[j];

                int c = -a - b;

                // 防止拆箱导致的空指针异常
                if (map.get(c) == null) {
                    continue;
                }

                int index = map.get(c);
                if (index == i || index == j) {
                    continue;
                }

                List<Integer> list = new ArrayList<>();

                if (!repeat.contains(a) || !repeat.contains(b) || !repeat.contains(c)) {
                    list.add(a);
                    list.add(b);
                    list.add(c);

                    repeat.add(a);
                    repeat.add(b);
                    repeat.add(c);

                    // leetcode输出的结果要求是升序的。。。。。不得已才写这个排序
                    list.sort((x, y) -> {
                        return x - y;
                    });

                    result.add(list);
                }
            }
        }

        return result;
    }


    public static void main(String[] args) {
        int nums[] = {-1, 0, 1, 2, -1, -4};
        System.out.println(threeSum(nums));
    }
}
