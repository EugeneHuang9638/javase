package io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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


    public static void main(String[] args) throws IOException {
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

            System.out.println("accepted connection from " + clientSocket);
            threadPoolExecutor.submit(() -> {
                InputStream inputStream;
                OutputStream out;
                try{
                    inputStream = clientSocket.getInputStream();
                    byte[] bytes = new byte[1024];
                    int length;
                    // read 方法在输入数据可用、检测到文件末尾或者抛出异常前，此方法一直阻塞
                    // 所以无法将"Hi avengerEug"写入到socket，进而客户端收不到消息
                    while ((length = inputStream.read(bytes)) != -1) {
                        System.out.println(new String(bytes, 0, length));
                        System.out.println(length);
                    }

                    out = clientSocket.getOutputStream();
                    out.write("Hi avengerEug\r\n".getBytes());
                    out.flush();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try{
                        clientSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
