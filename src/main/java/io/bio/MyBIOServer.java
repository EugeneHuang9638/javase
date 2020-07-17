package io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 实现一个BIO服务器
 */
public class MyBIOServer {


    public static void startConcurrecyServer() throws IOException {
        // 创建一个任务长度为10的线程池，同时工作的线程数为5, 最大线程数为10
        // 空闲时间为60s，拒绝策略为抛异常
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                5,
                10,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(20),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        ServerSocket socket = new ServerSocket(8800);
        while (true) {

            Socket clientSocket = socket.accept();

            threadPoolExecutor.submit(() -> {
                try{
                    System.out.println("accepted connection from " + clientSocket);
                    InputStream inputStream = clientSocket.getInputStream();
                    byte[] bytes = new byte[1024];
                    int length;
                    // read 方法在输入数据可用、检测到文件末尾或者抛出异常前，此方法一直阻塞
                    // 所以无法将"Hi avengerEug"写入到socket，进而客户端收不到消息
                    while ((length = inputStream.read(bytes)) != -1) {
                        System.out.println(new String(bytes, 0, length));
                        System.out.println(length);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void startCommonServer() throws IOException {

        ServerSocket socket = new ServerSocket(8800);
        while (true) {
            try{
                System.out.println("服务器启动，等待连接, 第一次阻塞");
                Socket clientSocket = socket.accept();
                System.out.println("accepted connection from " + clientSocket);
                InputStream inputStream = clientSocket.getInputStream();
                byte[] bytes = new byte[1024];
                int length;
                // read 方法在输入数据可用、检测到文件末尾或者抛出异常前，此方法一直阻塞
                // 所以无法将"Hi avengerEug"写入到socket，进而客户端收不到消息
                System.out.println("连接成功，准备读数据, 第二次阻塞");
                while ((length = inputStream.read(bytes)) != -1) {
                    System.out.println(length);
                    System.out.println(new String(bytes, 0, length));
                }
                System.out.println("数据读取成功，继续等待下一个客户端连接");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException {
        // startCommonServer();
        startConcurrecyServer();
    }
}
