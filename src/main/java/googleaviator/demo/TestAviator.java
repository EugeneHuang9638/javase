package googleaviator.demo;

import com.googlecode.aviator.AviatorEvaluator;

import java.util.HashMap;
import java.util.Map;

/**
 * 使用变量
 * @author muyang
 * @create 2024/7/10 19:02
 */
public class TestAviator {

    public static void main(String[] args) {
        String myName = "avengerEug";
        Map<String, Object> env = new HashMap<>();
        env.put("myName", myName);
        Map<String, Object> ext = new HashMap<>();
        ext.put("version", "100");
//        ext.put("test", "嘿嘿");
        env.put("ext", ext);
        // env是上下文，myName没有用单引号包裹，所以它是一个变量，默认为null，如果env中有相同的key，则会取对应的value
        // 任何对象与字符串相加，都是字符串
        String result = (String) AviatorEvaluator.execute("'hello ' + myName + '. version: ' + ext.version + ' 不存在的key：ext.test： ' + ext.test", env);
        System.out.println(result);
    }
}
