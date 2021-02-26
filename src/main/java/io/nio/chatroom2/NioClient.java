package io.nio.chatroom2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClient {
    //通道管理器
    private Selector selector;

    public static void main(String[] args) throws IOException {
        NioClient client = new NioClient();
        client.initClient("127.0.0.1", 8899);
        client.connect();
    }

    /**
     * 初始化客户端
     * 1、初始化selector
     * 2、将客户端的socketChannel注册到selector中，并对连接事件感兴趣
     *
     * @throws IOException
     */
    public void initClient(String host, int port) throws IOException {
        // Nio的通用写法，保证不阻塞
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);

        this.selector = Selector.open();
        // 连接客户端，可以理解成：客户端执行连接操作时，就会产生连接事件，此时这个事件还在socketChannel中。
        socketChannel.connect(new InetSocketAddress(host, port));
        // 将socketChannel注册到selector中，由selector来关联socketChannel的连接事件
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    public void connect() throws IOException {
        while (true) {
            /**
             * 在selector中寻找一次，看selector中是否有事件发生，如果没有时间发生，则会阻塞到这里。
             * 但由于我们在初始化客户端的过程中已经执行了连接服务器的操作 ---> 35行的代码
             * 因此此处不会阻塞，会往下执行
             *
             * 目前selector中只注册了一个socketChannel，且这个socketChannel对connect事件 “感兴趣”
             * 因此，此次寻找，就是看是否有socketChannel的connect事件发生，如果有发生，则会
             * 将这个事件放入到一个set集合中，并且解阻塞。
             *
             * 注意：
             */
            int select = selector.select();
            System.out.println(select);

            /**
             * 执行到这里，那肯定是上述的select方法解除阻塞了 ===> 这代表着有事件发生了，接下来需要处理事件了
             * 而此时的事件就是连接事件。SelectionKey对象可以描述当前是什么事件
             */
            Set<SelectionKey> set = this.selector.selectedKeys();
            System.out.println("产生的事件个数: ===> " + set.size());

            // 遍历迭代器，开始处理事件
            Iterator<SelectionKey> iterator = set.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                SocketChannel socketChannel = (SocketChannel) key.channel();
                socketChannel.configureBlocking(false);

                /**
                 * 验证key对应的通道是否已完成或未能完成其套接字连接操作 --> 这种情况都是返回true
                 * 若key对应的通道不支持链接时，才会返回false。
                 * 因此可以断定：只要是连接了客户端
                 */
                if (key.isConnectable()) {
                    /**
                     * 下面三行代码保证客户端能连接上服务器。
                     * 若连接失败，则会抛出异常，
                     * @see SocketChannel#finishConnect() 方法的签名
                     */
                    if (socketChannel.isConnectionPending()) {
                        socketChannel.finishConnect();
                    }

                    // 连接服务器成功后，向服务器发送一段信息：I have been connected server
                    socketChannel.write(ByteBuffer.wrap("I have been connected server".getBytes()));
                    // 关注读事件
//                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    // 接收服务端发来的消息
                    ByteBuffer byteBuffer = ByteBuffer.allocate(512);
                    int len;
                    while ((len = socketChannel.read(byteBuffer)) != -1) {
                        System.out.print(new String(byteBuffer.array(), 0, len));
                        byteBuffer.clear();
                    }
                }

                /**
                 * 处理完当前的事件后，要remove掉，否则在下一次select时发现还有事件未处理，则不会阻塞。
                 * 这样的话就会导致客户端这边在 一直无意义“空执行”。
                 * 可以把此段代码注释掉，然后运行，看是否一直在打印：“产生的事件个数: ===> 1” 的信息
                 */
                iterator.remove();
            }
        }
    }
}
