package loadbalancing;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * 带权重的负载均衡算法
 */
public class WeightLoadBalancing {


    /**
     * 第一个带权重负载均衡算法简易版本：
     *
     * 所谓带权重，我们可以把相同的ip在map中存储多个。
     * 然后用随机算法进行抽取
     *
     * 缺点：使用此种方式比较占用内存
     *
     */
    static class WeightLoadBalancing1 {

        private static Map<Integer, String> serviceContainer = new HashMap<>();

        private static Logger logger = Logger.getLogger("WeightLoadBalancing1");

        static {
            // 192.168.1.100的权重为3
            serviceContainer.put(0, "192.168.1.100");
            serviceContainer.put(1, "192.168.1.100");
            serviceContainer.put(2, "192.168.1.100");

            // 192.168.1.101的权重也为3
            serviceContainer.put(3, "192.168.1.101");
            serviceContainer.put(4, "192.168.1.102");
            serviceContainer.put(5, "192.168.1.103");

            // 192.168.1.104的权重为5
            serviceContainer.put(6, "192.168.1.104");
            serviceContainer.put(7, "192.168.1.104");
            serviceContainer.put(8, "192.168.1.104");
            serviceContainer.put(9, "192.168.1.104");
            serviceContainer.put(10, "192.168.1.104");

            // 下面的权重都为1
            serviceContainer.put(11, "192.168.1.105");
            serviceContainer.put(12, "192.168.1.106");
            serviceContainer.put(13, "192.168.1.107");
            serviceContainer.put(14, "192.168.1.108");
            serviceContainer.put(15, "192.168.1.109");
        }

        public static String random() {
            Random random = new Random();
            int randomNum = random.nextInt(16);
            logger.info("随机数: " + randomNum);
            // 范围[0,16)  ==>  0 - 15
            return serviceContainer.get(randomNum);
        }

        public static void main(String[] args) {
            logger.info("带权重的负载均衡后的服务IP: " + random());
        }
    }

    /**
     * 改进后的版本：
     *   我们可以根据版本一的思路进一步扩展，权重多代表的就是服务器性能好，
     *   那么在数轴中表示的是不是就是占用的线程长？
     *   于是我们用数轴来解决带权重负载均衡算法：
     *
     * ip:   101  102  103   104
     * 权重:  1 3   4   5   6   10
     * 数轴:  ---   -   -   -----
     *
     * 所以我们可以随机1-10的数字，得到随机数x
     * 如果x在1-3的区间内，则返回101的ip
     * 如果x等于4，则返回102
     * 如果x等于5，则返回103
     * 如果x处于6-10之间，则返回104
     */
    static class WeightLoadBalancing2 {
        private static Logger logger = Logger.getLogger("WeightLoadBalancing2");


        public static String random() {
            Random random = new Random();
            // random.nextInt(10) => [0, 10) 左开右闭区间的数字
            // random.nextInt(10) + 1 => [1, 11) 左开右闭区间的数字 ==>  [1, 10] 左开右开区间的数字
            int randomNum = random.nextInt(10) + 1;
            logger.info("随机后的数字为：" + randomNum);

            String result = null;

            if (randomNum <= 3) {
                result = "192.168.1.101";
            } else if (randomNum == 4) {
                result = "192.168.1.102";
            } else if (randomNum == 5) {
                result = "192.168.1.103";
            } else if (randomNum <= 10) {
                result = "192.168.1.104";
            }

            return result;
        }

        public static void main(String[] args) {
            logger.info("带权重的负载均衡后的服务IP: " + random());
        }
    }

}
