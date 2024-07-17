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

        Map<String, Object> map = new HashMap<>();
        map.put("date", new Date());
        map.put("tagId", 9L);

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
        // 从map中取数据
//        System.out.println("'today is ' + " + AviatorEvaluator.execute("mmap != nil && mmap.date" , env)); // mmap != nil返回的是一个boolean类型，mmap.date不是boolean类型，会报错
        System.out.println("'today is ' + " + AviatorEvaluator.execute("mmap.date" , env));
        System.out.println("'mmap.tagId == 9; result:' + " + AviatorEvaluator.execute("mmap.tagId == 9" , env));
        // 判断变量是否在set集合中存在
        System.out.println(AviatorEvaluator.execute("'element2 is include？: ' + include(set, 'element2')" , env));

    }

}
