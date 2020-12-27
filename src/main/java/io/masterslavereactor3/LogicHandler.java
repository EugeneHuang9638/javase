package io.masterslavereactor3;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 客户端向服务端发送数据时，会执行到此handler
 *
 * 此handler是一个slaveReactor对应一个handler
 */
public class LogicHandler {

    private static final String SPECIAL_COMMAND = "avengerEug";


    public void handler(SelectionKey registerKey) {
        try {
            readData(registerKey);
            writeData(registerKey);
        } catch (IOException e) {
            // 客户端强制关闭连接了，此处catch住异常，由服务器断开与客户端的连接
            try {
                closeClientConnection(registerKey);
            } catch (IOException e1) {
            }
        }
    }

    private void writeData(SelectionKey registerKey) throws IOException {
        SocketChannel sc = (SocketChannel) registerKey.channel();
        String content = "我收到你的消息了";
        System.out.println(Thread.currentThread().getName() + "开始向客户端发送数据, 内容：" + content);
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
        byteBuffer.put(content.getBytes());
        byteBuffer.flip();
        sc.write(byteBuffer);

//        registerKey.interestOps(SelectionKey.OP_READ);
//        registerKey.selector().wakeup();
    }

    private String readData(SelectionKey registerKey) throws IOException {
        SocketChannel sc = (SocketChannel) registerKey.channel();

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
            closeClientConnection(registerKey);
            return null;
        } else {
            if (SPECIAL_COMMAND.equals(stringBuffer.toString())) {
                // 如果命令匹配，则执行对应的特殊操作
                execLogic();
            } else {
                System.out.println("接收到客户端发送的消息：" + stringBuffer.toString());
//                registerKey.interestOps(SelectionKey.OP_WRITE);
//                registerKey.selector().wakeup();
            }
        }

        return stringBuffer.toString();
    }

    private void closeClientConnection(SelectionKey registerKey) throws IOException {
        System.out.println("客户端断开了连接, 取消对应的事件, 并关闭对应的channel");
        SocketChannel sc = (SocketChannel) registerKey.channel();
        registerKey.cancel();
        sc.close();
    }

    private void execLogic() {
        System.out.println("客户端发送了avengerEug指令，我要输出一段sql：SELECT * FROM user");
    }

}
