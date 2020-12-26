package io2.demo1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * NIO服务端
 */
public class NIOServer {


    public static void main(String[] args) throws IOException {
        startServer();
    }

    /**
     * 启动服务器
     * @throws IOException
     */
    private static void startServer() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8080));

        // 打开多路复用器
        Selector selector = Selector.open();

        /**
         * 将服务端的channel注册到多路复用器上，并设置感兴趣的事件为 “接收连接” 事件
         * 当多路复用器轮训（无事件时，会阻塞）时，只要有客户端连接到了服务器，就会解除阻塞
         */
        SelectionKey selectionKey = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (!Thread.interrupted()) {
            System.out.println("等待注册到多路复用器的channel对应绑定的事件发生");
            /**
             * 轮询多路复用器，若注册到多路复用器的channel中无感兴趣的事件发生，则会阻塞在这里.r
             * 当有感兴趣的事件发生时，就会解除阻塞，继续往下执行。
             *
             * 其实这个方法也是阻塞的。但是为什么要称NIO是非阻塞的呢？
             * 因为这个方法阻塞的时候表明程序是“静止”的，什么叫静止？
             * 即无任何客户端来操作服务器，那既然没有任何客户端来连接服务器，那为什么不阻塞呢？难道还让他空跑浪费
             * 服务器资源？而只要有客户端对服务器有任何操作，这个方法就不会有阻塞的情况发生，
             * 因此，这是NIO非阻塞的一个点。
             */
            selector.select();

            /**
             * 执行到这里则说明，一定有感兴趣的事件发生了，此时我们直接
             * 拿到对应的selectionKey，此key就是当初我们注册到多路复用器时返回的key
             * @see selectionKey
             * 我们可以通过这个key拿到当初注册的到多路复用器的channel
             */
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                handle(next);
                iterator.remove();;
            }
        }
    }

    private static void handle(SelectionKey next) throws IOException {
        if (next.isAcceptable()) {
            System.out.println("注册到多路复用器的服务器的channel有感兴趣的事件发生了，事件为：客户端连接服务器的连接事件");

            /**
             * 此处发生的是客户端连接服务器的事件，我们要做的事情就是把连接到客户端的channel也注册到多路复用器中统一管理
             * 1、拿到selectionKey对应的服务器channel
             */
            ServerSocketChannel channel = (ServerSocketChannel) next.channel();

            /**
             * 2、拿到连接到服务器的客户端channel
             *
             * accept其实是阻塞的，那他什么时候会解阻塞呢？
             * 当有客户端连接时，就会变成非阻塞。而在此，我们有个前提条件，就是有客户端连接了，我们
             * 才调用了这个accept方法，那此时是不是就变成了非阻塞了呢？
             * 这是NIO非阻塞的另外一个点。
             */
            SocketChannel accept = channel.accept();
            System.out.println("有客户端连接了：" + accept.getRemoteAddress());
            // 3、配置非阻塞
            accept.configureBlocking(false);
            // 4、客户端channel也注册到多路复用器，并对读取事件感兴趣
            accept.register(next.selector(), SelectionKey.OP_READ);
        } else if (next.isReadable()) {
            System.out.println("注册到多路复用器的客户端的channel向服务端写数据了，触发了channel的读事件了");
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            SocketChannel channel = (SocketChannel) next.channel();
            channel.read(byteBuffer);

            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.limit() - byteBuffer.position()];
            byteBuffer.get(bytes);
            System.out.println("接收到客户端传来的消息：" + new String(bytes));

            // 写数据，告诉客户端
            channel.write(ByteBuffer.wrap("I have accepted your message".getBytes()));
//            channel.close();
        } else if (next.isValid()) {
            System.out.println("服务端端口连接");
        }
    }
}
