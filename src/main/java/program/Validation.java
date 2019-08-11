package program;

import java.util.*;

/**
 * 程序化:
 *
 * 如果26个英文字母 A B C D EF G H I J K L M N O P Q R S T U V W X Y Z 分别等于:
 * 1 2 3 4 5 6 7 8 9 10 11 12 13 14 1516 17 18 19 20 21 22 23 24 25 26。那么：
 * Knowledge (知识)： K+N+O+W+L+E+D+G+E＝ 11+14+15+23+12+5+4+7+5=96%。
 * Workhard (努力工作）：W+O+R+K+H+A+R+D＝ 23+15+18+11+8+1+18+4 =98%。
 * 也就是说知识和努力工作对我们人生的影响可以达到96％和98％。
 * Luck（好运） L+U+C+K＝12+21+3+11=47%。
 * Love（爱情） L+O+V+E＝12+15+22+5=54%。
 * 看来，这些我们通常认为重要的东西却并没起到最重要的作用。
 * 那么，什么可以决定我们100％的人生呢？
 * 是Money（金钱）吗？M+O+N+E+Y=13+15+14+5+25=72%。
 * 看来也不是。
 * 是Leadership (领导能力)吗？
 * L+E+A+D+E+R+S+H+I+P=12+5+1+4+5+18+19+9+16=89%
 * 还不是。
 * 金钱，权力也不能完全决定我们的生活。那是什么呢？
 * 其实，真正能使我们生活圆满的东西就在我们自己身上！
 * ATTITUDE (心态) A+T+T+I+T+U+D+E＝1+20+20+9+20+21+4+5=100%。
 * 我们对待人生的态度才能够100％的影响我们的生活，或者说能够使我们的生活达到 100%的圆满！
 */
public class Validation {

    private static final int endIndex = 90;

    private static int charStartIndex = 65;

    public static Map<Character, Integer> charMapping;

    static {
        charMapping = new HashMap<>();
        initCharMapping();
    }

    private static void initCharMapping() {
        int index = 1;
        while (charStartIndex <= endIndex) {
            charMapping.put((char)(charStartIndex++), index++);
        }
        System.out.println(charMapping);
    }

    private static String calculationWord(String word) {
        char[] chars = word.toUpperCase().toCharArray();
        int count = 0;
        StringBuilder expression = new StringBuilder("");

        for (int i = 0; i < chars.length; i++) {
            int value = charMapping.get(chars[i]);
            count += value;

            if (i != chars.length - 1) {
                expression.append(value).append((char) 43);
            } else {
                int indexInner = expression.lastIndexOf("+");
                expression.replace(indexInner, indexInner + 1, "=" + count).append((char) 37);
            }
        }

        System.out.println(word + " ==> " + expression);
        return count + (char)37 + "";
    }

    public static void main(String[] args) {
        calculationWord("Knowledge");
        calculationWord("Workhard");
        calculationWord("Luck");
        calculationWord("Love");
        calculationWord("Money");
        calculationWord("Leadership");
        calculationWord("Attitude");
    }
}
