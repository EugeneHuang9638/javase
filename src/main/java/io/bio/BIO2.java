package io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BIO2 {

    public static void main(String[] args) throws IOException {
        startCommonServer();
    }

    public static void startCommonServer() throws IOException {
        ServerSocket socket = new ServerSocket(8800);
        while (true) {
            try{
                System.out.println("服务器启动，等待连接, 第一次阻塞");
                Socket clientSocket = socket.accept();
                System.out.println("客户端连接成功： " + clientSocket);
                new Thread(() -> {
                    InputStream inputStream = null;
                    try {
                        inputStream = clientSocket.getInputStream();
                        byte[] bytes = new byte[1024];
                        int length;
                        System.out.println("连接成功，准备读数据, 第二次阻塞");
                        while ((length = inputStream.read(bytes)) != -1) {
                            System.out.println(length);
                            System.out.println(new String(bytes, 0, length));
                        }
                        System.out.println("数据读取成功，继续等待下一个客户端连接");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }).start();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // 关闭input流代码省略
            }
        }
    }
}
