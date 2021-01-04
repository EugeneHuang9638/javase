package io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BIO1 {

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
                InputStream inputStream = clientSocket.getInputStream(); // 1
                byte[] bytes = new byte[1024];							 // 2
                int length;
                // read 方法在读取完数据时或者抛出异常前，此方法一直阻塞
                // 所以无法将"Hi avengerEug"写入到socket，进而客户端收不到消息
                System.out.println("连接成功，准备读数据, 第二次阻塞");
                while ((length = inputStream.read(bytes)) != -1) {
                    System.out.println(length);
                    System.out.println(new String(bytes, 0, length));
                }
                System.out.println("数据读取成功，继续等待下一个客户端连接");

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // 关闭input流代码省略
            }
        }
    }
}
