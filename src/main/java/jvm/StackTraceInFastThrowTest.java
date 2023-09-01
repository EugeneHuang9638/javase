package jvm;

/**
 *
 * jvm参数：OmitStackTraceInFastThrow
 * 含义：省略异常栈信息，快速抛出异常。 当异常抛出2w+的时候，就会触发fast throw机制。此时异常堆栈会被忽略
 *
 * 默认情况下 :OmitStackTraceInFastThrow和StackTraceInThrowable都为true
 * 当满足条件：(!StackTraceInThrowable || OmitStackTraceInFastThrow) 时才会
 * 开启fast throw
 * 因为OmitStackTraceInFastThrow默认为true，默认符合条件。
 * 如果想关闭，jvm需要添加此参数: -XX:-OmitStackTraceInFastThrow
 * 如果想打开，jvm添加此参数：-XX:+OmitStackTraceInFastThrow   或者不加，默认开启
 *
 * @author avengerEug
 * @create 2023/9/1 16:02
 */
public class StackTraceInFastThrowTest {


    public static void main(String[] args) throws InterruptedException {
        int x = 0;
        while (true) {
            try {
                System.out.println(1 / x);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(2);
            }
        }
    }
}
