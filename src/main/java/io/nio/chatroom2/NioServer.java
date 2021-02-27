package io.nio.chatroom2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {

    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        ssc.bind(new InetSocketAddress("127.0.0.1", 8899));

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            System.out.println("等待事件发生");
            // 同理，只要ssc中有事件发生，这里就会解除阻塞
            selector.select();

            // 获取ssc关注的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                // 第一次进来，肯定是连接事件
                if (key.isAcceptable()) {
                    System.out.println("有客户端连接，客户端连接事件发生了");
                    /**
                     * 有客户端连接了，准备接收客户端发送的数据。
                     * 因此，需要获取到serverSocketChannel（这个inner和上面第15行代码开启的ssc是同一个对象 --> 两个对象的地址相同），
                     * 并且注册read事件来保证下一次selectedKeys方法能够获取到它
                     */
                    ServerSocketChannel inner = (ServerSocketChannel) key.channel();
                    /**
                     * 与bio一样，获取连接到服务器的socket。
                     * 其实这段代码也是阻塞的。只不过我是先有客户端连接才执行这行代码。
                     * 所以，这段代码在这种逻辑下是不阻塞的。
                     * 这里拿到的clientSocketChannel与客户端的clientSocketChannel不是同一个对象，
                     * 但是是有关联的，你通过debug后就会发现，其内部有一个remote属性，指向的就是远程的客户端。
                     *
                     * 获取到指向客户端的clientSocketChannel后，也把他注册到selector中，由selector来管理
                     * 指向客户端的clientSocketChannel。
                     *
                     * 因此，此时服务端的selector中分别管理了服务端和指向客户端的channel。
                     * 因此，服务端可以处理服务端的连接事件以及客户端的写事件（客户端往channel写数据，服务端对应的就是读事件）
                     */
                    SocketChannel clientSocketChannel = inner.accept();
                    clientSocketChannel.configureBlocking(false);
                    clientSocketChannel.register(selector, SelectionKey.OP_READ);
                } else if (key.isReadable()) {
                    /**
                     * 进入到这里面来了，那肯定就是客户端向服务端发送数据了，因为此时是读事件，
                     * 上面分析到了: 此时的selector管理者服务端的连接事件以及客户端的读事件。
                     * 涉及到ByteBuffer的操作
                     */
                    System.out.println("有客户端向服务端发送数据了。");
                    // 拿到指向客户端的clientSocketChannel，读取内部的内容
                    SocketChannel clientSocketChannel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(10);
                    StringBuilder sb = new StringBuilder();
                    while (clientSocketChannel.read(buffer) > 0) {
                        buffer.flip();
                        sb.append(new String(buffer.array(), 0, (buffer.limit() - buffer.position())));
                    }
                    System.out.println("客户端发来的信息：" + sb);

                    /**
                     * 读完客户端的数据后，往客户端回发消息。
                     * 准备好了数据后，执行write方法。此时会触发写事件。
                     * 因为服务端的selector中关联的指向客户端的clientSocketChannel目前只对
                     * 读事件感兴趣，因此此时还要为他注册写事件。这个时候有两种方式
                     * 第一种：
                     *   使用key来配置感兴趣的事件：key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                     * 第二种：
                     *   使用register方式：clientSocketChannel.register(selector, SelectionKey.OP_WRITE);
                     *
                     * 注册完了之后，服务端的selector管理的指向客户端的clientSocketChannel中就会对读、写事件感兴趣了。
                     * 进而可以处理读写事件了。
                     */
                    ByteBuffer bufferToWrite = ByteBuffer.wrap("HelloClient. I'm server".getBytes());
                    clientSocketChannel.write(bufferToWrite);
                    clientSocketChannel.register(selector, SelectionKey.OP_WRITE);
                } else if (key.isWritable()) {
                    System.out.println("write事件");
                    /**
                     * NIO事件触发是水平触发
                     * 使用Java的NIO编程的时候，在没有数据可以往外写的时候要取消写事件，
                     * 在有数据往外写的时候再注册写事件。
                     *
                     * 由于register后的事件无法取消注册，因此使用key的interestOps的方式来变更事件是更好的一种选择。
                     */
                    key.interestOps(SelectionKey.OP_READ);
                }

                iterator.remove();
            }
        }
    }
}
