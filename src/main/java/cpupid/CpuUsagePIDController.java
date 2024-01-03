package cpupid;

/**
 *
 * @create 2024/1/2 17:41
 */
public class CpuUsagePIDController {
    private double kp; // 比例系数
    private double ki; // 积分系数
    private double kd; // 微分系数
    private double targetUsage; // 目标 CPU 使用率
    private double integral; // 误差积分
    private double lastError; // 上一次误差

    public CpuUsagePIDController(double kp, double ki, double kd, double targetUsage) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.targetUsage = targetUsage;
        this.integral = 0;
        this.lastError = 0;
    }

    public void monitorCpuUsage(double currentUsage, double deltaTime) {
        // 计算误差
        double error = targetUsage - currentUsage;

        // 积分项累加
        integral += error * deltaTime;

        // 微分项
        double derivative = (error - lastError) / deltaTime;

        // PID 控制器输出（此处不直接使用输出来控制，仅用于决策）
        double output = kp * error + ki * integral + kd * derivative;

        // 根据输出值打印日志
        if (output > 0) {
            // 超过了60%
            System.out.println("负载"); // CPU 使用率超过目标值
        } else {
            // 降低到了60%
            System.out.println("恢复"); // CPU 使用率低于目标值
        }

        // 更新上一次误差
        lastError = error;
    }


    public static void main(String[] args) {
        double kp = 0.5; // 比例系数
        double ki = 0.1; // 积分系数
        double kd = 0.1; // 微分系数
        double targetUsage = 60.0; // 目标 CPU 使用率，60%

        CpuUsagePIDController pidController = new CpuUsagePIDController(kp, ki, kd, targetUsage);

        // 模拟持续监控 CPU 使用率
        double currentUsage; // 当前 CPU 使用率（模拟数据）
        double deltaTime = 1.0; // 模拟每次更新的时间间隔，单位秒

        // 模拟数据，实际应用中需要替换为真实的 CPU 使用率获取方式
        double[] sampleUsages = {55.0, 65.0, 60.0, 70.0, 58.0, 63.0};

        for (double usage : sampleUsages) {
            currentUsage = usage;
            pidController.monitorCpuUsage(currentUsage, deltaTime);
        }
    }

}

