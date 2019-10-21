package program;

import java.util.HashMap;
import java.util.Map;

/**
 * Map<String, Object> 格式的map, 若put进去的类型是int类型,
 * 那么在get出来时, 虽然是object类型, 但是会默认转成int,
 * 所以get出来的类型会根据你put进去的类型转换, 此时若使用Long
 * 来接收, 那么会抛出类型转换异常。
 */
public class TestMap {
    private static Map<String, Object> map;

    static {
        map = new HashMap<>();
        map.put("test", 123);
        map.put("test2", 111L);
    }

    public static void main(String[] args) {
        // 会报类型转型错误, Integer 不能直接转 Long
        long test = (long)((int) map.get("test"));
    }

    public static <T> T getKey(String key) {
        return (T)map.get(key);
    }
}
