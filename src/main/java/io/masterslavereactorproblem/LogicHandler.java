package io.masterslavereactorproblem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LogicHandler  {

    private static final String SPECIAL_COMMAND = "avengerEug";

    private SocketChannel sc;

    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            5,
            10,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(30),
            Executors.defaultThreadFactory(),
            (r, executor) -> System.out.println("自定义拒绝策略")
    );

    // 当前处理read事件的channel绑定的key
    private SelectionKey registerKey;

    public LogicHandler(SocketChannel sc, SelectionKey registerKey) {
        this.sc = sc;
        this.registerKey = registerKey;
    }

    public void run() {
        // 死循环执行
        System.out.println("111111111111111111111");

//        if (registerKey.interestOps() == SelectionKey.OP_READ) {
//            threadPoolExecutor.execute(() -> {
//                try {
//                    readData();
//                    writeData();
//                } catch (IOException e) {
//                    // 客户端强制关闭连接了，此处catch住异常，由服务器断开与客户端的连接
//                    try {
//                        closeClientConnection();
//                    } catch (IOException e1) {
//                    }
//                }
//            });
//        }
    }

    private void writeData() throws IOException {
        String content = "我收到你的消息了";
        System.out.println(Thread.currentThread().getName() + "开始向客户端发送数据, 内容：" + content);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        byteBuffer.put(content.getBytes());
        byteBuffer.flip();
        sc.write(byteBuffer);
    }

    private void readData() throws IOException {

        System.out.println(Thread.currentThread().getName() + " 开始读取客户端发送给服务端的数据");
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(8);
        StringBuffer stringBuffer = new StringBuffer();
        int length;
        while ((length = sc.read(byteBuffer)) > 0) {
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.limit() - byteBuffer.position()];
            byteBuffer.get(bytes);
            stringBuffer.append(new String(bytes));
            byteBuffer.clear();
        }

        if (length == -1) {
            closeClientConnection();
            return;
        } else {
            if (SPECIAL_COMMAND.equals(stringBuffer.toString())) {
                // 如果命令匹配，则执行对应的特殊操作
                execLogic();
            } else {
                System.out.println("接收到客户端发送的消息：" + stringBuffer.toString());
            }
        }
    }

    private void closeClientConnection() throws IOException {
        System.out.println("客户端断开了连接, 取消对应的事件, 并关闭对应的channel");
        registerKey.cancel();
        sc.close();
    }

    private void execLogic() {
        System.out.println("客户端发送了avengerEug指令，我要输出一段sql：SELECT * FROM user");
    }
}
