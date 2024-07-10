package googleaviator.demo;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorBoolean;
import com.googlecode.aviator.runtime.type.AviatorObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义函数实现功能：目标值格式为1,2,3,4
 * 目标值为x
 * 要求判断：x是否在1，2，3，4中
 * @author muyang
 * @create 2024/7/10 19:39
 */
public class TestAviatorExistElementMethod {

    public static void main(String[] args) {
        // 注册函数
        AviatorEvaluator.addFunction(new IsExistElement());

        Map<String, Object> env = new HashMap<>();
        env.put("appkey", "11234");
        Object execute = AviatorEvaluator.execute("isExistElement(appkey, '11,34,223,1123,11234')", env);
        System.out.println(execute instanceof Boolean);
        System.out.println(execute);
    }

    private static class IsExistElement extends AbstractFunction {

        /**
         * arg1对应在上下文中的值是否在arg2中
         * @param env
         * @param arg1
         * @param arg2
         * @return
         */
        @Override
        public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
            // 候选值
            String candidateValue = FunctionUtils.getStringValue(arg1, env);
            // 如果env中没有对应的key，则返回本身
            String targetValues = FunctionUtils.getStringValue(arg2, env);
            String[] split = StringUtils.split(targetValues, ",");
            for (String val : split) {
                if (Objects.equals(val, candidateValue)) {
                    return AviatorBoolean.TRUE;
                }
            }

            return AviatorBoolean.FALSE;
        }

        @Override
        public String getName() {
            return "isExistElement";
        }
    }

}
