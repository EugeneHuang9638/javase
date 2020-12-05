package io.nio.chatroom.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

/**
 * 聊天室服务器
 *
 *
 *
 */
public class ChatRoomServer {

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

            //


        } catch (IOException e) {
            e.printStackTrace();
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
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 99778));
    }

}
