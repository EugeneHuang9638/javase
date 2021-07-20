package leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * 给定两个字符串 s 和 t ，编写一个函数来判断 t 是否是 s 的字母异位词（长度一样，包含的字母都一样，每个字符出现的频率也一样，只是顺序不同而已）。
 *
 * 示例 1:
 * 输入: s = "anagram", t = "nagaram"
 * 输出: true
 *
 * 示例 2:
 * 输入: s = "rat", t = "car"
 * 输出: false
 *
 * 说明:
 * 你可以假设字符串只包含小写字母。
 *
 *
 * 分析：
 *   就是给定两个字符串，判断两个字符串中的字母和出现的次数是不是一样的，
 *   同时位置要不一样(即不能是同一个字符串)
 *
 * 解法:
 *   暴力破解法：
 *     即对两个字符串进行排序，然后挨个元素判断是否相等
 *     暴力破解法的时间复杂度为：要排序两个数组，排序中最快的是快排NlogN
 *     所以最终是2*NlogN == NlogN
 *   使用hash表：
 *     因为此问题的本质是判断两个字符串中的字符串是否一致，以及出现的数量
 *     是否一致，所以我们可以使用一个map，key为字符，value为出现的次数
 *
 */
public class ValidAnagram {


    private static void populateMap(Map<String, Integer> map, String[] strings) {
        for (String sArr : strings) {
            if (map.get(sArr) == null) {
                map.put(sArr, 1);
            } else {
                int count = map.get(sArr);
                map.put(sArr, count + 1);
            }
        }
    }

    public static boolean isAnagram(String s, String t) {

        if (s == null || t == null) {
            return false;
        }

        String[] sArrs = s.split("");
        String[] tArrs = t.split("");
        HashMap<String, Integer> sMap = new HashMap<>();
        HashMap<String, Integer> tMap = new HashMap<>();

        populateMap(sMap, sArrs);
        populateMap(tMap, tArrs);

        return sMap.equals(tMap);
    }


    public static void main(String[] args) {
        System.out.println(isAnagram("rat", "car"));
        System.out.println(isAnagram("anagram", "nagaram"));
    }
}
