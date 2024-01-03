package cpupid;

import java.util.HashMap;
import java.util.Map;

/**
 * 用一个简单的java程序，模拟一个PID控制器用于控制汽车速度的场景。
 * 假设我么你的目标是将汽车速度维持在60公里每小时。
 * 在这个模拟中，我们使用一个变量speed来代表当前速度，targetSpeed为我们的目标速度。
 * 我们将模拟油门输入(即PID控制器的输出)，它将根据误差、误差的积累以及误差变化率来调整当前速度。
 *
 *
 * PID算法：OP(t) = P(t) + I(t) + D(t)
 * 其中：
 * P(t) = Kp * e(t)         比例项
 * I(t) = Ki * ∫e(t) dt     积分项
 * D(t) = Kd * de(t)/dt     微分项
 * 备注：e(t)：是实际值与目标值的差值。
 * ∫e(t) dt：是从0到当前时间t的误差积分（积分就是在一定时间范围内做累加的操作，也就是把过去所有的误差值按时间加起来）
 * de(t)/dt：随误差e(t) 随时间变化的率，即误差的导数（变化率），它表示误差在多块的速度上升或下降。
 *
 *
 * @create 2024/1/2 15:57
 */
public class CarSpeedPIDController {

    private double kp; // 比例系数
    private double ki; // 积分系数
    private double kd; // 微分系数
    private double targetSpeed; // 目标速度
    private double integral; // 误差积分
    private double lastError; // 上一次误差

    // 存储每次控制的误差
    private double error;

    public CarSpeedPIDController(double kp, double ki, double kd, double targetSpeed) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.targetSpeed = targetSpeed;
        this.integral = 0;
        this.lastError = 0;
    }

    public Map<String, Double> calculateThrottle(double currentSpeed, double deltaTime) {
        Map<String, Double> map = new HashMap<String, Double>();
        // 计算误差
        this.error = targetSpeed - currentSpeed;
        map.put("error", error);

        // 积分项累加（错误量）
        integral += this.error * deltaTime;
        map.put("integral", integral);

        // 微分项：计算误差变化率 = (当前错误 - 上一次错误)  / 时间间隔
        double derivative = (this.error - lastError) / deltaTime;
        map.put("derivative", integral);

        // PID 控制器输出
        double throttle = kp * error + ki * integral + kd * derivative;
        map.put("throttle", throttle);

        // 更新上一次误差
        lastError = error;
        map.put("lastError", lastError);

        // 返回油门值
        return map;
    }

    /**
     * 在这个模拟中，我们初始化 CarSpeedPIDController 类的实例，然后在一个循环中不断地计算新的油门输入，并更新当前速度。由于实际的车辆动力学非常复杂，这里我们做了简化，假设油门输入直接与速度增量成正比。
     * 请注意，这个例子非常简化，实际中 PID 控制器会考虑更多的动力学因素如阻力、车辆质量和轮胎的牵引力等。此外，PID 控制器的参数（kp、ki、kd）通常需要通过反复试验来调整，以便在实际应用中获得最佳性能。
     * @param args
     */
    public static void main(String[] args) {
        double kp = 0.1; // 比例项: 基于当前的误差值进行控制。p = kp * t. 其中kp为比例系数，t为误差。
        double ki = 0.01; // 积分项：基于误差随时间的累积总和进行控制。
        double kd = 0.05; // 微分系数：基于误差变化率进行控制。 决定了PID控制器输出与误差变化率（误差的变化速度）
        double targetSpeed = 60; // 目标速度，60 公里每小时

        CarSpeedPIDController pidController = new CarSpeedPIDController(kp, ki, kd, targetSpeed);

        double currentSpeed = 0; // 当前速度
        double deltaTime = 0.1; // 模拟每次更新的时间间隔，单位秒

        // 模拟 60 秒内速度控制
        for (int i = 0; i < 600; i++) {
            // 计算油门输入
            Map<String, Double> map = pidController.calculateThrottle(currentSpeed, deltaTime);

            // 根据油门输入更新当前速度，这里简化假设增加的速度公式为：speed increment = throttle * deltaTime
            currentSpeed += map.get("throttle") * deltaTime;

            // 输出当前速度
            System.out.printf("Time: %.1f sec, Current Speed: %.2f km/h，误差：%.1f, 积分项：%.1f, 微分项：%.1f  PID控制器输出: %.1f  上一次误差：%.1f \n",
                    i * deltaTime,
                    currentSpeed,
                    map.get("error"),
                    map.get("integral"),
                    map.get("derivative"),
                    map.get("throttle"),
                    map.get("lastError"));
        }
    }

}
