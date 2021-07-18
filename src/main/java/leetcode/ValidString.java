package leetcode;

import java.util.*;

/**
 * 给定一个只包括 '('，')'，'{'，'}'，'['，']' 的字符串，判断字符串是否有效。
 *
 * 有效字符串需满足：
 *
 * 左括号必须用相同类型的右括号闭合。
 * 左括号必须以正确的顺序闭合。
 * 注意空字符串可被认为是有效字符串。
 *
 * 示例 1:
 *
 * 输入: "()"
 * 输出: true
 * 示例 2:
 *
 * 输入: "()[]{}"
 * 输出: true
 * 示例 3:
 *
 * 输入: "(]"
 * 输出: false
 * 示例 4:
 *
 * 输入: "([)]"
 * 输出: false
 * 示例 5:
 *
 * 输入: "{[]}"
 * 输出: true
 *
 *
 * 分析:
 *
 * 定义一个栈，
 * 当第一次遇到左括号就压栈，
 *   当遇到右括号时，则跟栈顶的元素进行匹配，若匹配成功，则将栈顶元素弹出去
 *   所以若是一个符合条件的字符串，最后栈里的元素肯定是空的，
 * 当第一次遇到右括号时，直接不合法
 *
 */
public class ValidString {

    public static boolean valid(String s) {
        if (s == null) {
            return false;
        }

        // 空字符串也算合法
        if (s.trim().length() == 0) {
            return true;
        }

        // 添加一个映射关系，key为所有的右括号
        Map<String, String> map = new HashMap<>();
        map.put(")", "(");
        map.put("}", "{");
        map.put("]", "[");

        String strings[] = s.split("");

        // 栈，只存储左括号
        Stack<String> stack = new Stack<>();

        for (String string : strings) {
            // 根据string获取value
            String innerVal = map.get(string);

            if (innerVal == null) {
                // 当前string为左括号，将左括号压栈
                stack.push(string);
            } else {

                // 若栈为空，且要压栈的元素是右括号 ===> 非法字符串
                if (stack.size() == 0) {
                    return false;
                }

                // 否则, string的值为右括号，开始出栈比对
                // 此时将当前栈顶的元素(左括号)和string(右括号)进行括号匹配
                // peek是拿栈顶的元素
                String stackPop = stack.peek();
                // 匹配
                if (stackPop.equals(map.get(string))) {
                    // pop拿到栈顶元素并出栈
                    stack.pop();
                    continue;
                }

                break;

            }
        }

        return stack.size() == 0;
    }



    public static void main(String[] args) {
        String target = "((({[]})))";
        System.out.println(valid(target));
    }

    public static void validate(String s) {

    }


}
