package leetcode.dynamicprograming;

import java.util.HashMap;
import java.util.Map;

/**
 * 从斐波拉契数列为demo进入动态规划的世界
 *
 * 动态规划(Dynamic Programing)概念：
 *   我们一般都会讲到人生规划、职业规划。规划二字给人的感觉就是对某一件事长久的计划，
 *   比如为了高考、考研、进大厂而制定的学习计划，高瞻远瞩，有长远的计划。
 *   在动态规划的编程题上，动态规划其实就是动态递推。类似于高中数学“数学归纳法”的知识点。
 *   解决动态规划题目的核心就是找到递推关系式(数学归纳法的公式)，这一步往往是最难的，
 *   因为递推关系式出来了，问题就解决一半了。因此，对于动态规划算法题，我们需要致力于
 *   寻找递推关系式。作为初学者，咱们不能一步就登峰造极，得一步一步(似爪牙，像魔鬼的步伐)
 *   的循序渐进的去找。
 *
 * 动态规划的几个核心概念：
 *  1. 递归 + 记忆化 => 递推
 *  2. 状态的定义： opt[n], dp[n], fib[n]
 *  3. 状态转移方程：opt[n] = best_of(opt[n - 1], opt[n - 2], .....)
 *  4. 最优子结构
 *
 */
public class DemoFib {

    /**
     * fib(0) fib(1) fib(2) fib(3) fib(4)  fib(5)
     *   0      1      1      2      3      5
     *
     *
     * 使用递归的话，我们可以发现，很多节点都重复计算了，所以我们现在
     * 要做记忆化操作 ===> 加缓存
     *
     * 时间复杂度: o(2的(n - 1)次方)，n为层数
     *
     * @param n
     * @return
     */
    public static int fib(int n) {
        return n <= 1 ? n : fib(n - 1) + fib(n - 2);
    }

    private static Map<Integer, Integer> cache = new HashMap<>();

    /**
     * 优化了逻辑，添加了缓存
     *
     * 变成了o(n)的时间复杂度了
     *
     * @param n
     * @return
     */
    public static int fibCache(int n) {
        if (cache.get(n) == null) {
            cache.put(n, n <= 1 ? n : fibCache(n - 1) + fibCache(n - 2));
        }
        return cache.get(n);
    }

    /**
     *  递推：
     *    从结果开始往上推，
     *    f(0) = 0
     *    f(1) = 1
     *    f(2) = f(0) + f(1)
     *    f(3) = f(1) + f(2)
     *    f(4) = f(3) + f(2) = f(1) + f(2) + f(2) = f(1) + f(0) + f(1) + f(0) + f(1) = 3f(1) + 2f(0) = 3 + 0 = 3
     *
     *
     *    所以其实，
     *
     * @param n
     * @return
     */
    public static int dynamicFib(int n) {
        int opt[] = new int[n + 1];
        opt[0] = 0;
        opt[1] = 1;

        for (int i = 2; i <= n; i++) {
            opt[i] = opt[i - 1] + opt[i -2];
        }

        return opt[n];
    }

    public static void main(String[] args) {
        System.out.println(fib(5));
        System.out.println(fibCache(5));
        System.out.println(dynamicFib(5));
    }
}
