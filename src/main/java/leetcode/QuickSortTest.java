package leetcode;

import java.util.Arrays;

/**
 * 快排算法
 */
public class QuickSortTest {


    /**
     * 快速排序算法逻辑
     * @param arr 需要被排序的算法
     * @param low 遍历数组的头指针
     * @param high 遍历数组的尾指针
     * @return
     */
    public static void sort(int arr[], int low, int high) {
        System.out.println("--------------开始新的排序--------");
        // 低位指针 大于 高位指针，直接返回，不需要再排序
        if (low >= high) {
            return;
        }

        int splitIndex = getSplitIndex(arr, low, high);

        System.out.println("找到相同的下标了，划分数组。下标为：" + splitIndex);
        System.out.println("此时的数组结构为：");
        Arrays.stream(arr).forEach(System.out::print);
        sort(arr, low, splitIndex - 1);
        sort(arr, splitIndex + 1, high);
    }

    private static int getSplitIndex(int[] arr, int low, int high) {
        // 将下标为low的元素作为基数
        int baseNum = arr[low];

        // while循环执行完毕后，基准值的左边都是小于它的值，基准值的右边都是大于它的值
        while (low < high) {
            // 右边往左遍历，找到小于temp元素的下标
            while (arr[high] >= baseNum && low < high) {
                high--;
            }
            System.out.println("从右往左遍历，找到的数为：" + arr[high] + "，下标为：" + high);
            // 做赋值 --> 将找到的元素赋值到baseNum的下标处
            arr[low] = arr[high];

            // 左边往右遍历，若元素小于baseNum，则继续移动。直到遇到大于等于 temp的元素时，停下来
            while (arr[low] < baseNum && low < high) {
                low++;
            }
            arr[high] = arr[low];

            System.out.println("从左往右遍历，找到的数为：" + arr[low] + "，下标为：" + low);

            // 执行到下面来了，则表示找到了元素中大于等于temp的元素了，此时的下标为low
            // 执行到下面来了，则表示找到了左边大于等于temp的元素，以及右边小于temp的元素，做数据交换
        }

        arr[low] = baseNum;
        return low;
    }


    public static void main(String[] args) {
        int arr[] = {2, 1, 1, 9, 10, 4, 0};
        sort(arr, 0, arr.length - 1);
        System.out.println("排序完毕，结果为：");
        Arrays.stream(arr).forEach(item -> System.out.print(item + " "));
    }

}
