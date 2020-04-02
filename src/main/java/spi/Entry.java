package spi;

import java.util.Iterator;
import java.util.ServiceLoader;

public class Entry {

    public static void main(String[] args) {
        ServiceLoader<IndexService> serviceLoader = ServiceLoader.load(IndexService.class);

        // 测试java spi
        // 1. 缺点: 需要使用迭代器来获取所有的服务，无按需查找api
        // 2. 假设提供的服务类依赖于其他类, 无法自动添加依赖
        Iterator iterator = serviceLoader.iterator();
        while (iterator.hasNext()) {
            IndexService indexService = (IndexService) iterator.next();
            indexService.index();
        }
    }
}
