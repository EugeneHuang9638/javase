package loadbalancing;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * 随机负载均衡算法
 */
public class RandomLoadBalancing {


    public static Map<Integer, String> serviceContainer = new HashMap<>();

    private static Logger logger = Logger.getLogger("RandomLoadBalancing");

    static {
        serviceContainer.put(0, "192.168.1.100");
        serviceContainer.put(1, "192.168.1.101");
        serviceContainer.put(2, "192.168.1.102");
        serviceContainer.put(3, "192.168.1.103");
        serviceContainer.put(4, "192.168.1.104");
        serviceContainer.put(5, "192.168.1.105");
        serviceContainer.put(6, "192.168.1.106");
        serviceContainer.put(7, "192.168.1.107");
        serviceContainer.put(8, "192.168.1.108");
        serviceContainer.put(9, "192.168.1.109");
    }

    public static String random() {
        Random random = new Random();
        // 范围[0,10)  ==>  0 - 9
        int randomNum = random.nextInt(10);
        logger.info("随机数：" + random);
        return serviceContainer.get(randomNum);
    }

    public static void main(String[] args) {
        logger.info("随机负载均衡后的IP: " + random());
    }

}
