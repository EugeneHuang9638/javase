package googleaviator.demo;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试自定义函数
 * @author muyang
 * @create 2024/7/10 19:15
 */
public class TestAviatorCustomMethod {

    public static void main(String[] args) {
        AviatorEvaluator.addFunction(new AddFunction());
        System.out.println(AviatorEvaluator.execute("add(1,2)"));

        Map<String, Object> env = new HashMap<>();
        env.put("left", 2);
        env.put("right", 20);

        // 如果为add(1,2,3) 则需要实现带3个AviatorObject参数的call方法
        System.out.println(AviatorEvaluator.execute("add(left,right)", env));
    }

    /**
     * 自定义了一个叫add的自定义函数
     */
    private static class AddFunction extends AbstractFunction {

        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            //

            Number left = FunctionUtils.getNumberValue(arg1, env);
            Number right = FunctionUtils.getNumberValue(arg2, env);

            return new AviatorDouble(left.doubleValue() + right.doubleValue());
        }

        @Override
        public String getName() {
            return "add";
        }
    }

}
