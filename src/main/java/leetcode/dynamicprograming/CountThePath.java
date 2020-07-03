package leetcode.dynamicprograming;

/**
 *
 * 长为W个格子，宽为H格子
 *
 */
public class CountThePath {


    /**
     * 从p[0][0]到p[p.length][p[0].length] 有多少种走法
     * @param p
     * @return
     */
    public static int path(boolean p[][], int wIndex, int hIndex) {
        // 节点超出二维数组返回 或者 节点为石头，则返回0
        if (wIndex >= p.length || hIndex >= p[0].length || !p[wIndex][hIndex]) {
            return 0;
        }

        // 到达终点后，返回1
        if (wIndex == p.length - 1 && hIndex == p[0].length - 1) {
            return 1;
        }

        return path(p, wIndex + 1, hIndex) + path(p, wIndex, hIndex + 1);

    }


    /**
     * 递归 + 缓存  自底向上
     * @param p
     * @return
     */
    public static int pathDynamic(boolean p[][]) {
        int dp[][] = new int[p.length][p[0].length];

        for (int i = p.length - 2; i >= 0; i--) {
            // 初始化当前行的最右边的列为1
            dp[i][p[i].length - 1] = 1;

            for (int j = p[i].length - 2; j >= 0; j--) {
                // 初始化当前列的最下面的行为1
                dp[p.length - 1][j] = 1;

                dp[i][j] = !p[i][j] ? 0 : dp[i][j + 1] + dp[i + 1][j];
            }
        }

        return dp[0][0];

    }


    public static void main(String[] args) {
        // 5行，4列  ==>
        // 行：[0, 4]  [0, p.lenth - 1]
        // 列：[0, 3]  [0, p[0].length - 1]
        boolean p[][] = new boolean[][] {
                {true, true, true, true},
                {true, true, false, true},
                {true, false, true, true},
                {true, true, true, true},
                {true, true, true, true},
        };

        System.out.println(path(p, 0, 0));
        System.out.println(pathDynamic(p));
    }


}
