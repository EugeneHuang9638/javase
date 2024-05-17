package pid;

/**
 * @author muyang
 * @create 2024/4/7 20:51
 */
public class CustomerPID {

    public static void main(String[] args) throws InterruptedException {
        PIDController pid = new PIDController(0.01, 0.15, 0.6, 60.0); // 这些参数 kp, ki, kd 需要根据实际情况调整

        while (true) {
            double actualCpuUsage = CpuMonitor.getCpuUsage();
            double output = pid.calculate(actualCpuUsage);

            if (output > 0) {
                // PID 控制器的输出大于0，意味着需要降低 CPU 使用率（因为实际使用率超过了设定点）
                limitTraffic(actualCpuUsage); // 调用你的限流逻辑
            } else {
                System.out.println("actualCpuUsage: " + actualCpuUsage + "未       执行限流操作"); // 在这里实现你的限流逻辑
            }

            // 等待20ms后再次采样
            Thread.sleep(20);
        }
    }

    private static void limitTraffic(double actualCpuUsage) {
        System.out.println("actualCpuUsage: " + actualCpuUsage + "执行限流操作"); // 在这里实现你的限流逻辑
        // 警告：该代码行为示例用途，需要用到实际的限流机制替换它。
    }


    public static class CpuMonitor {
        // 假设你有一个方法来获取CPU的当前使用率
        public static double getCpuUsage() {
            // 这里替换成真实获取CPU使用率的代码
            return Math.random() * 100; // 0到100%的模拟值
        }
    }

    public static class PIDController {
        private double kp; // 比例系数
        private double ki; // 积分系数
        private double kd; // 微分系数
        private double setpoint; // 目标值
        private double integral = 0;
        private double lastError = 0;
        private double lastTime = System.currentTimeMillis();

        public PIDController(double kp, double ki, double kd, double setpoint) {
            this.kp = kp;
            this.ki = ki;
            this.kd = kd;
            this.setpoint = setpoint;
        }

        public double calculate(double actual) {
            double currentTime = System.currentTimeMillis();
            // 增量时间
            double deltaTime = (currentTime - lastTime) / 1000.0; // 以秒为单位
            // 误差
            double error = setpoint - actual;
            integral += error * deltaTime;
            // 当前时刻与上一时刻的误差
            double derivative = (error - lastError) / deltaTime;
            lastError = error;
            lastTime = currentTime;
            return kp * error + ki * integral + kd * derivative;
        }
    }

}
