package io.nio.chatroom.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 聊天室服务器
 */
public class ChatRoomServer {

    private static final int TIME_OUT = 2000;

    private static AtomicInteger integer = new AtomicInteger(1);


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
     * 占用端口：9977
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
                // 轮询选择select
                pollingSelector();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 每隔两秒轮训一次，看注册到selector中的channel对应感兴趣的事件是否发生
     * jdk在进行selector轮询时，会出现bug，bug描述可参考如下地址：
     * https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6670302
     *
     * 大致含义是：在jdk6环境下，
     * 如果在window中，它运行得非常正常。
     * 但是在linux中，我们虽然设置了2s轮询一次，但是在select中为0的情况下，
     * 可能会出现死循环的情况，即一直空轮训（不再是2s轮训一次了，而是死循环的轮训） --> 造成cpu飙升至100%.
     * TODO 需要研究为什么会这样，以及selector的运行原理
     *
     * @throws IOException
     */
    private static void pollingSelector() throws IOException {
        long start = System.nanoTime();
        selector.select(TIME_OUT);
        long end = System.nanoTime();
        // 将2000毫秒转成纳秒TimeUnit.MILLISECONDS.toNanos(TIME_OUT)
        if (end - start >= TimeUnit.MILLISECONDS.toNanos(TIME_OUT)) {
            // 未发生bug，直接设置成1
            integer.set(1);
        } else {
            // 发生bug了， 加1
            integer.addAndGet(1);
        }

        // 如果bug轮训次数达到了10次，则重新构建selector
        if (integer.get() == 10) {
            rebuildSelector();
        }


        /**
         * 遍历selector中的所有channel，挨个校验是否有感兴趣的事情发生
         *
         * selector.keys()和selector.selectedKeys()方法区别
         * 它们是包含关系，前者包含了后者。
         * 前者是返回所有注册到selector的channel对应的事件。
         * 后者是返回所有已经发生的事件
         *
         * 比如：一个有两个socketChannel注册到了selector中。
         * 一个socketChannel对connect事件感兴趣
         * 另外一个socketChannel对read事件感兴趣
         *
         * 假设此时，客户端向服务端发送了数据，
         * 此时我们执行selector的keys方法时，会返回两个SelectionKey，分别是connect相关的和read相关的。
         * 若我们执行selectedKeys方法时，此时只会返回read相关的selectionKey
         *
         * 因此：我们可以得出结论：selectedKeys返回的是已经被selector感知发生的事件
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
                    System.out.println("in while ==> " + length);
                    byteBuffer.flip();
                    // 创建byte数组接收参数
                    byte[] bytes = new byte[byteBuffer.limit() - byteBuffer.position()];
                    byteBuffer.get(bytes);
                    sendAllOfClient(bytes, socketChannel);
                    byteBuffer.clear();
                }

                System.out.println("out while, 无数据可读了, length为0 " + length);

                if (length < 0) {
                    // 客户端断开连接  --> 从select中移除
                    next.cancel();
                    System.out.println("客户端： " + socketChannel + " 下线了");
                }
            }

            // 连接事件
            if (next.isAcceptable()) {
                // 获取连接到聊天室服务器的客户端socket, 并注册到selector中
                registerClientSocket();
            }

            // 移除当前的事件
            iterator.remove();
        }
    }

    /**
     * 重新构建selector
     */
    private static void rebuildSelector() throws IOException {
        Selector newSelector = Selector.open();
        // 将旧selector中的事件及其绑定的东西转移到新selector中去
        for (SelectionKey key : selector.keys()) {
            int interestOps = key.interestOps();
            key.channel().register(newSelector, interestOps);

            // 将原来的selector中感兴趣的事件给取消掉，因为最终要释放旧的selector
            key.cancel();
        }
        selector.close();

        // 赋值新的selector
        selector = newSelector;
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
            // 防止有其他的selectedKey执行了cancel方法，因为执行了cancel方法后，selectedKey依然不会被移除，只是被标识了
            if (next.isValid()) {
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
            System.out.println("客户端：" + accept + " 上线了");
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
