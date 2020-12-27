package io.masterslavereactor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 7788);
        OutputStream outputStream = socket.getOutputStream();
        Scanner scanner = null;

        Thread thread = new Thread(() -> {
            try {
                InputStream inputStream = socket.getInputStream();
                byte[] bytes = new byte[1024];
                /**
                 * 这里会抛异常，原因就是判断close和读操作不是一个原子操作
                 * 有可能我校验的时候，还没有关闭，但是我在读取的时候就关闭了
                 *
                 * TODO 待解决：当我在控制台输入exit命令时，可以优雅的退出
                 */
                while (!Thread.interrupted()) {
                    if (!socket.isClosed() && inputStream.read(bytes) > 0) {
                        System.out.println(new String(bytes));
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        thread.start();
        while (true) {
            scanner = new Scanner(System.in);
            // 设置scanner遇到换行符才读取数据，默认是遇到空格和换行都会读取数据的
            scanner.useDelimiter("\n");
            String next = scanner.next();

            if ("exit".equals(next)) {
                thread.interrupt();
                // TODO 待解决：当客户端强制关闭进程时，服务器如何优雅的感知到下线，而不是抛出异常，让服务器挂掉
                socket.close();
                break;
            }

            outputStream.write(next.getBytes());
            outputStream.flush();
        }

    }
}
