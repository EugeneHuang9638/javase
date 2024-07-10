package googleaviator.demo;

import com.googlecode.aviator.AviatorEvaluator;

/**
 * @author muyang
 * @create 2024/7/10 19:10
 */
public class TestAviatorExec {

    public static void main(String[] args) {
        String myName = "avengerEug";
        String result = (String) AviatorEvaluator.exec("'hello ' + asdfsadf", myName);
        System.out.println(result);
    }
}
