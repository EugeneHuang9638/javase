package io.nio.chatroom.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 聊天室服务器
 */
public class ChatRoomServer {


    public static void main(String[] args) {
        // 开启服务器
        ChatRoomServer.start();
    }

    /**
     * nio的selector
     *
     * 用来管理服务端的channel和客户端的channel
     *
     * 同时用来管理服务端和客户端channel互相感兴趣的事情
     */
    private static Selector selector;

    /**
     * 服务端socket（nio）
     */
    private static ServerSocketChannel serverSocketChannel;


    /**
     * 开启一个聊天室服务器
     * 占用端口：99778
     *
     */
    public static void start() {
        try {
            // 初始化nio的selector
            initSelector();
            // 初始化服务聊天室socket
            initServerSocket();

            // 将服务端的serverSocketChannel绑定到selector中
            registerServerSocketChannelToSelector();

            while (true) {
                // 获取连接到聊天室服务器的客户端socket, 并注册到selector中
                registerClientSocket();

                // 每隔两秒轮训一次，看注册到selector中的channel对应感兴趣的事件是否发生
                selector.select(2000);

                /**
                 * 遍历selector中的所有channel，挨个校验是否有感兴趣的事情发生
                 *
                 * TODO 需要区分selectedKeys和keys的区别
                 */
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();

                    // 如果当前的selectedKey是读取事件，则读取数据
                    if (next.isReadable()) {
                        // 获取selector中的channel
                        SocketChannel socketChannel = (SocketChannel) next.channel();

                        // 读取消息，并进行转发
                        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
                        int length = 0;
                        while ((length = socketChannel.read(byteBuffer)) > 0) {
                            byteBuffer.flip();
                            // 创建byte数组接收参数
                            byte[] bytes = new byte[byteBuffer.limit() - byteBuffer.position()];
                            byteBuffer.get(bytes);
                            sendAllOfClient(bytes, socketChannel);
                            byteBuffer.clear();
                        }

                        if (length < 0) {
                            // 客户端断开连接  --> 从select中移除
                            next.cancel();
                            System.out.println("客户端 " + socketChannel + " 下线了");
                        }
                    }

                    // 移除当前的事件
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void registerServerSocketChannelToSelector() {
        try {
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }

    }

    /**
     * 群发到所有客户端（不发送给自己）
     * @param bytes
     * @param socketChannel
     */
    private static void sendAllOfClient(byte[] bytes, SocketChannel socketChannel) {
        // 获取所有的客户端
        Set<SelectionKey> keys = selector.keys();
        Iterator<SelectionKey> iterator = keys.iterator();
        while (iterator.hasNext()) {
            SelectionKey next = iterator.next();
            SelectableChannel channel = next.channel();
            // 过滤掉服务端，只对客户端进行群发消息
            if (channel instanceof SocketChannel) {
                if (channel != socketChannel) {
                    // 如何过滤掉自己
                    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                    try {
                        ((SocketChannel) channel).write(byteBuffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private static void registerClientSocket() throws IOException {
        SocketChannel accept = serverSocketChannel.accept();
        if (accept != null) {
            /**
             * public static final int OP_ACCEPT = 1 << 0;
             * 1 * 2的0次方 = 1
             * 将连接到服务器的socket注册到selector中
             *
             * 且表明此channel对客户端连接到服务器的socket的读取数据事件感兴趣
             */
            accept.configureBlocking(false);
            accept.register(selector, SelectionKey.OP_READ);
            System.out.println("有客户端连接了：" + accept);
        }
    }

    private static void initSelector() {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initServerSocket() throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        // 配置服务聊天室是非阻塞的
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 9977));
    }

}
