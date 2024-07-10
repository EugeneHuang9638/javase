package googleaviator.demo;

import com.googlecode.aviator.AviatorEvaluator;

/**
 * 测试调用aviator内置函数
 * @author muyang
 * @create 2024/7/10 19:12
 */
public class TestAviatorCallMethod {


    public static void main(String[] args) {
        // 获取传入字符串的长度
        AviatorEvaluator.execute("string.length('hello')");  // 5

        // string.substring('hello', 1, 2) 返回e。test字符串中包含字母e
        AviatorEvaluator.execute("string.contains(\"test\", string.substring('hello', 1, 2))");  // true
    }

}
