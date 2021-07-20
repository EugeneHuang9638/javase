package leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
 *
 * 你可以假设每种输入只会对应一个答案。但是，数组中同一个元素不能使用两遍。
 *
 * 示例:
 *
 * 给定 nums = [2, 7, 11, 15], target = 9
 *
 * 因为 nums[0] + nums[1] = 2 + 7 = 9
 * 所以返回 [0, 1]
 *
 *
 * 分析：
 *   就是要计算数组中的哪两个元素的和等于target
 *
 * 解法
 *   暴力破解法：
 *     两层循环，循环x，内部再嵌套循环y，计算 x + y 是否等于9
 *   使用HashSet：
 *     因为要求 x + y = 9; 所以我们可以将表达式修改下变成
 *     x = 9 - y
 *     所以，首先我们可以在hashMap集合中存储数组的整个值,
 *     key为value, index为对应下标的值
 *     (这里不能用set，因为有可能数组中有重复的元素，
 *     而题目最开始说了每个元素只能用一次，因为set是
 *     无重复的, 假设数组中有重复的值那就乱了。所以我们要
 *     规定，若计算出来的下标一致，那么则不进行数组下标赋值)
 *
 *     首先，我们的map中存储的时数组中每个value对应的下标，
 *     在计算两数之和时，给定一个结果，然后我们遍历数组，
 *     相当于现在要去计算数组中是否存在另外一个符合条件的值，
 *     因此，这个查找符合条件的值的过程就放在map中去做了
 *
 *
 */
public class TwoSum {

    public static int[] twoSum(int[] nums, int target) {
        int [] result = new int[2];
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], i);
        }
        map.keySet().forEach(item -> System.out.println("key: " + item + ", value：" + map.get(item)));

        for (int i = 0; i < nums.length; i++) {
            int num2 = target - nums[i];
            if (map.get(num2) == null) {
                continue;
            }

            // 前面判空了，防止拆箱出现空指针异常
            int num2Index = map.get(num2);
            if (num2Index == i) {
                continue;
            }

            result[0] = i;
            result[1] = num2Index;
            break;

        }

        return result;
    }

    public static void main(String[] args) {
        int[] nums = new int[] {1, 4, 2, 1};
        int target = 2;
        int[] ints = twoSum2(nums, target);
        if (ints != null) {
            for (int anInt : ints) {
                System.out.println(anInt);
            }
        }
        System.out.println();
    }




    public static int[] twoSum2(int [] arr, int sumValue) {
        int [] result = new int[2];
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            // 1 -> 0, 1 -> 1, 2 -> 2, 6 -> 3
            map.put(arr[i], i);
        }

        for (int i = 0; i < arr.length; i++) {
            int target = sumValue - arr[i];
            // 从map中去找，看数组中是否满足符合条件的值
            Integer integer = map.get(target);
            if (integer != null) {
                // 找到了符合条件的值
                result[0] = i;
                result[1] = integer;
                return result;
            }
        }

        return null;
    }
}
