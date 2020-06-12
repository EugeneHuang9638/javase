package leetcode;

/**
 * 给定一个数m和n，返回的m的n次方。
 *
 * 主要是使用分治的思想来解决这个问题
 *
 */
public class PowerOfNumber {


    /**
     * 给定一个数m和n，返回的m的n次方。
     *
     * 抽象出子问题：
     *   假设n为偶数，那么就是分成求2的2分之n次方， 结果就是两个2的2分支n次方相乘
     *
     *   假设n为偶数，那么就是分成求2的2分之n次方，结果就是两个n的2分之n次方相乘再乘以2
     *
     * @param m
     * @param n
     * @return
     */
    public static int handle(int m, int n) {
        if (n == 1) {
            return m;
        }

        return n % 1 == 0 ? handle(m,n/2) * handle(m, n/2) : handle(m * m, n/2) * handle(m, n/2);
    }

    public static void main(String[] args) {
        System.out.println(handle(3, 4));
    }
}
