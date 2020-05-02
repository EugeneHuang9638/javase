package leetcode;

import java.util.HashSet;
import java.util.Set;

/**
 * 题目: 无重复字符的最长子串
 *
 *  给定一个字符串，请你找出其中不含有重复字符的 最长子串 的长度。
 *
 * 示例 1:
 *
 * 输入: "abcabcbb"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "abc"，所以其长度为 3。

 * 示例 2:
 * 输入: "bbbbb"
 * 输出: 1
 * 解释: 因为无重复字符的最长子串是 "b"，所以其长度为 1。

 * 示例 3:
 * 输入: "pwwkew"
 * 输出: 3
 * 解释: 因为无重复字符的最长子串是 "wke"，所以其长度为 3。
 *      请注意，你的答案必须是 子串 的长度，"pwke" 是一个子序列，不是子串。
 *
 * 解释下:
 * 子序列为串中连续的任意字符
 * 无重复字符的最长字串应该是连续的子序列中无重复字符
 *
 *
 */
public class LengthOfLongestSubstring {

    /**
     * 自己的解法:
     *   时间复杂度: o(m * (m -1))  ==>  m为字符串的长度
     *
     * 在leetcode中
     * 显示详情
     * 执行用时 :
     * 334 ms
     * , 在所有 Java 提交中击败了
     * 5.74%
     * 的用户
     * 内存消耗 :
     * 40.6 MB
     * , 在所有 Java 提交中击败了
     * 5.20%
     * 的用户
     *
     * 可以看出，性能不太好
     *
     * @param s
     * @return
     */
    public static int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        if (s.trim().length() == 0) {
            return 1;
        }

        String longestSubString = "";

        char[] chars = s.toCharArray();
        // 遍历每一个元素，存储它对应的无重复字符的子串
        for (int i = 0; i < chars.length; i++) {
            char currentChar = chars[i];
            String container = String.valueOf(currentChar);
            if (longestSubString.length() > (chars.length - i + 1)) {
                break;
            }
            for (int j = i + 1; j < chars.length; j++) {
                String afterString = String.valueOf(chars[j]);

                // 如果包含，则存储当前的字符串作为当前字符的子串
                if (container.indexOf(afterString) != -1) {
                    break;
                } else {
                    container += afterString;
                }
            }

            if (container.length() > longestSubString.length()) {
                longestSubString = container;
            }
        }

        return longestSubString.length();
    }

    /**
     * 官方解答，使用 滑动窗口
     *
     * 所谓滑动窗口: 就是将一个字符对应的无重复子串。
     * 将无重复子串都作为一个窗口，每次都对此窗口进行处理，
     * (因为此窗口内部已经是一些无重复的子串了，我们可以记录
     * 到无重复子串的最后一个字符的index，后续将基于这个index
     * 往后处理，如果index后面的字符不在这个窗口中，那么则把它
     * 加入到窗口中，否则当前窗口就是当前处理字符串的无重复子串)
     *
     * 我们不妨以 abcabcbb 为例，找出 从每一个字符开始的，
     * 不包含重复字符的最长子串，那么其中最长的那个字符串即为答案。对于示例
     * 一中的字符串，我们列举出这些结果，其中括号中表示选中的字符以及最长的字符串：
     *
     * 以 (a)bcabcbb 开始的最长字符串为 (abc)abcbb；   ---> 括号里面的就是 "滑动窗口"
     * 以 a(b)cabcbb 开始的最长字符串为 a(bca)bcbb；
     * 以 ab(c)abcbb 开始的最长字符串为 ab(cab)cbb；
     * 以 abc(a)bcbb 开始的最长字符串为 abc(abc)bb；
     * 以 abca(b)cbb 开始的最长字符串为 abca(bc)bb；
     * 以 abcab(c)bb 开始的最长字符串为 abcab(cb)b；
     * 以 abcabc(b)b 开始的最长字符串为 abcabc(b)b；
     * 以 abcabcb(b) 开始的最长字符串为 abcabcb(b)。
     * 发现了什么？如果我们依次递增地枚举子串的起始位置，那么子串的结束位置也是递增的！
     * 这里的原因在于，假设我们选择字符串中的第 k 个字符作为起始位置，并且得到了不包
     * 含重复字符的最长子串的结束位置为 rk。那么当我们选择第 k+1 个字符作为起始位置时
     * ，首先从 k+1 到 rk的字符显然是不重复的，并且由于少了原本的第 k 个字符，我们可
     * 以尝试继续增大 rk​直到右侧出现了重复字符为止。
     *
     * 这样以来，我们就可以使用「滑动窗口」来解决这个问题了：
     * 1. 我们使用两个指针表示字符串中的某个子串（的左右边界，后续将从左边界开启寻找无重复字符的子串，从右边界开始搜索后续的字符
     *    , 若后续的字符在子串中重复了，那么子串的值就是当前左边界要寻找的无重复字符的子串）。
     *    其中左指针代表着上文中「枚举子串的起始位置」，而右指针即为上文中的 rk
     * 2. 在每一步的操作中，我们会将左指针向右移动一格，表示 我们开始枚举下一个字符作为起始位置，
     *    然后我们可以不断地向右移动右指针，但需要保证这两个指针对应的子串中没有重复的字符。在移动结束后，
     *    这个子串就对应着 以左指针开始的，不包含重复字符的最长子串。我们记录下这个子串的长度；
     * 3. 在枚举结束后，我们找到的最长的子串的长度即为答案。
     *
     * @param s
     * @return
     */
    public static int lengthOfLongestSubstringForOfficial(String s) {
        // 哈希集合，记录字符串中当前处理的字符对应的无重复字符的子串
        // 比如，针对pwwkew, 遍历它的每个字符
        // 当处理p时，occ存储的是pw, 其中i = 0, rk = 1
        // 当处理w时，occ存储的是w, 其中i = 1, rk = 1
        // 当处理w时，occ存储的是wke, 其中i = 2, rk = 4
        // 当处理k时，occ存储的是kew, 其中i = 3, rk = 5
        // 当处理e时，occ存储的是ew, 其中i = 4, rk = 5
        // 档处理w时，occ存储的是w, 其中i = 5, rk = 5
        Set<Character> occ = new HashSet<Character>();
        int n = s.length();
        // rk记录的是当前处理的字符对应的无重复字符的子串的最后一个char的index
        // ans是存储当前字符对应的无重复子串的长度
        int rk = 0, ans = 0;
        for (int i = 0; i < n; ++i) {
            // 此while循环的功效是将当前要遍历的char，包括自己以及后面的所有未重复的字符串添加到
            // occ中，遇到重复的就break
            while (rk < n && !occ.contains(s.charAt(rk))) {
                // 不断地移动右指针
                occ.add(s.charAt(rk));
                ++rk;
            }
            // 第 i 到 rk 个字符是一个极长的无重复字符子串
            // rk - i可能不太好理解，其实这个表达式的值就是occ集合的长度
            // 因为i 到 rk下标中的元素都会存在occ中
            // ans = Math.max(ans, rk - i);
            ans = Math.max(ans, occ.size());

            // 这里打印一下occ，验证下上面的分析
            System.out.println(occ);
            System.out.println("当前处理的字符为: " + s.charAt(i));
            System.out.println("当前处理的字符的下标为: " + i);
            System.out.println("当前处理的字符对应的无重复字符的子串为: " + occ);
            System.out.println("当前处理的字符对应的无重复字符的子串的最后一个字符的index为: " + rk);
            System.out.println("当前处理的字符对应的无重复字符的子串的长度为: " + occ.size());
            System.out.println("当前处理的字符对应的无重复字符的子串的长度为: " + (rk - i));

            // 将当前遍历的前一个元素从occ中移除掉
            if (i != 0) {
                // 左指针向右移动一格，移除一个字符
                occ.remove(s.charAt(i - 1));
            }
        }
        return ans;
    }


    public static void main(String[] args) {
        System.out.println(lengthOfLongestSubstringForOfficial("pwwkew"));
    }
}
