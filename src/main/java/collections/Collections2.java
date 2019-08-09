package collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Collections2 {

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");

        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if ("1".equals(item)) {
                iterator.remove();
            }
        }

        System.out.println(list);

    }
}
