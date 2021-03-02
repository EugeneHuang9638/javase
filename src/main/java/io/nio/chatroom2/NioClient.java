package io.nio.chatroom2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioClient {

    public static void main(String[] args) throws IOException {
        // Nio的通用写法，保证不阻塞，注意clientSocketChannel这个名称，这个是客户端的socketChannel
        SocketChannel clientSocketChannel = SocketChannel.open();
        clientSocketChannel.configureBlocking(false);

        Selector selector = Selector.open();
        // 连接客户端，可以理解成：客户端执行连接操作时，就会产生连接事件，此时这个事件还在socketChannel中。
        clientSocketChannel.connect(new InetSocketAddress("127.0.0.1", 8899));
        // 将socketChannel注册到selector中，由selector来关联socketChannel的连接事件
        clientSocketChannel.register(selector, SelectionKey.OP_CONNECT);

        while (true) {
            /**
             * 在selector中寻找一次，看selector中是否有事件发生，如果没有时间发生，则会阻塞到这里。
             * 但由于我们在初始化客户端的过程中已经执行了连接服务器的操作 ---> 35行的代码
             * 因此此处不会阻塞，会往下执行
             *
             * 目前selector中只注册了一个clientSocketChannel，且这个clientSocketChannel对connect事件 “感兴趣”
             * 因此，此次寻找，就是看是否有clientSocketChannel的connect事件发生，如果有发生，则会
             * 将这个事件放入到一个set集合中，并且解阻塞。
             *
             * 注意：虽然注册只有socketChannel的connect事件，但只要socketChannel有事件发生了，这个地方
             * 都会解除阻塞。
             * 举个例子：
             *   客户端拿到clientSocketChannel主动连接服务端，会产生connect事件，因此这个select方法会解除阻塞
             *   客户端拿到clientSocketChannel向服务端写数据，会产生write事件，这个select也会解除阻塞
             *   服务端拿到客户端的clientSocketChannel并往里面写数据，对于客户端而言：会产生read事件，这个select也会解除阻塞
             *   .....
             *
             * 因此，这个地方比较重要的点就是：
             * 只要注册到selector中的channel有事件发生，则会解除阻塞，如果这个事件是channel感兴趣的事件，则会进入set集合中
             */
            int select = selector.select();

            /**
             * 执行到这里，那肯定是上述的select方法解除阻塞了 ===> 这代表着有感兴趣的事件发生了，接下来需要处理感兴趣的事件了
             * 而此时的事件就是连接事件。SelectionKey对象可以描述当前是什么事件。
             *
             * 这个set集合只会保存感兴趣的事件
             */
            Set<SelectionKey> set = selector.selectedKeys();
            System.out.println("产生的事件个数: ===> " + set.size());

            // 遍历迭代器，开始处理事件
            Iterator<SelectionKey> iterator = set.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                // 这个socketChannel与上述的clientSocketChannel一致
                SocketChannel socketChannel = (SocketChannel) key.channel();

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
                    /**
                     * 还记得上面的select方法么。select方法是 只要有事件发生了就会解除阻塞。
                     * 而selectedKeys方法是 只筛选出感兴趣的事件。在此处注册一个read事件的原因就是
                     * 等会服务端会拿到客户端的clientSocketChannel并往里面写数据，
                     * 对客户端而言：这个操作就是一个读事件。因此这里注册一个读事件，为了就是能
                     * 在selectedKeys方法中把这个读事件找出来。不然的话select方法将会一直
                     * 解除阻塞，因为里面一直有一个读事件没有被处理，而且clientSocketChannel也
                     * 对读事件不感兴趣，最终会导致外层的while循环因select没有阻塞，会一直执行。
                     *
                     * 可以测试：将这段代码注释掉，看客户端是否一直在打印：产生的事件个数: ===> 0
                     */
                    socketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    /**
                     * 这里就是处理服务端发来的消息，就是使用ByteBuffer操作channel中的数据。
                     * 这里模拟了传输大数据量的情况，设置的byteBuffer的数组长度只有5。
                     * 这里涉及到channel的read方法，当read方法返回值 > 0 时，表示读取到的数据。
                     * 可能为0 或者 -1. 具体情况可以查看read方法的注释
                     * @see ReadableByteChannel#read(java.nio.ByteBuffer)
                     */
                    ByteBuffer byteBuffer = ByteBuffer.allocate(5);
                    int len;
                    while ((len = socketChannel.read(byteBuffer)) > 0) {
                        System.out.print(new String(byteBuffer.array(), 0, len));
                        byteBuffer.flip();
                    }
                }

                // 感兴趣的事件处理完毕后，移除掉
                iterator.remove();
            }
        }
    }

}
