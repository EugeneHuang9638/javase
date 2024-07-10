package googleaviator.demo;

import com.google.common.collect.Lists;
import com.googlecode.aviator.AviatorEvaluator;

import java.util.*;

/**
 * 测试动态表达式操作集合
 * @author muyang
 * @create 2024/7/10 19:25
 */
public class TestAviatorCollection {

    public static void main(String[] args) {
        List<String> list = Lists.newArrayList("hello", "world");

        int []array = new int[] {1, 2, 3};

        Map<String, Date> map = new HashMap<String, Date>();
        map.put("date", new Date());

        Set<String> set = new HashSet<String>();
        set.add("element1");
        set.add("element2");

        Map<String, Object> env = new HashMap<>();
        env.put("list", list);
        env.put("array", array);
        env.put("mmap", map);
        env.put("set", set);

        // 结论：数组和list访问用[1]的方式
        System.out.println(AviatorEvaluator.execute("list[0]+list[1]", env));
        System.out.println(AviatorEvaluator.execute("'array[0]+array[1] = ' + (array[0] + array[1])" , env));
        System.out.println(AviatorEvaluator.execute("'today is ' + mmap.date" , env));
        // 判断变量是否在set集合中存在
        System.out.println(AviatorEvaluator.execute("'element2 is include？: ' + include(set, 'element2')" , env));

    }

}
