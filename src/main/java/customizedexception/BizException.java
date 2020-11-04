package customizedexception;

public class BizException extends Exception {

    /**
     * 重写fillInStackTrace方法，解除业务异常产生调用栈的消耗
     * ps: 异常的发生，最终会产生调用栈，而调用栈构建需要调用到jdk的native方法，
     * 是有很大消耗的。
     * @return
     */
    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

    public static void main(String[] args) throws BizException {
        throw new BizException();
    }
}
